/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model

import com.google.android.gms.maps.model.LatLng
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.data.model.abst.Convertible
import jp.gaprot.omosanmaps.data.store.model.util.convert
import jp.gaprot.omosanmaps.data.store.model.util.realmImageUrls
import jp.gaprot.omosanmaps.map.LatLngFactory
import jp.gaprot.omosanmaps.map.toCsv
import java.util.*

open class RealmPlacemark(

        @PrimaryKey
        var name: String = "",

        var description: String = "",

        var folderName: String = "",

        @Ignore
        var latitude: Double = 0.0,

        @Ignore
        var longitude: Double = 0.0,

        var distance: Int = 0,

        var realmImageUrls: RealmList<RealmString> = RealmList(),

        var styleUrl: String = ""

) : RealmObject(), Convertible<Placemark> {

    @Ignore
    var latlng = LatLng(latitude, longitude)

    var coordinate = latlng.toCsv()

    constructor(placemark: Placemark) : this(
            name = placemark.name,
            description = placemark.description,
            folderName = placemark.folderName,
            latitude = placemark.latitude,
            longitude = placemark.longitude,
            distance = placemark.distance,
            realmImageUrls = placemark.realmImageUrls(),
            styleUrl = placemark.styleUrl
    )

    override fun convert(): Placemark {
        return Placemark.Builder()
                .setName(name)
                .setDescription(description)
                .setFolderName(folderName)
                .setLatLng(LatLngFactory.from(coordinate))
                .setDistance(distance)
                .setImageUrls(realmImageUrls.convert())
                .setStyleUrl(styleUrl)
                .build()
    }

    class QueryBuilder {

        private var folderName: String? = null

        private var maxDistance: Int? = null

        private var positions: List<String> = ArrayList()

        fun setFolderName(name: String?): QueryBuilder {
            this.folderName = name
            return this
        }

        fun setMaxDistance(distance: Int?): QueryBuilder {
            this.maxDistance = distance
            return this
        }

        fun setPositions(positions: List<String>): QueryBuilder {
            this.positions = positions
            return this
        }

        fun create(realm: Realm): RealmQuery<RealmPlacemark> {
            val query = realm.where(RealmPlacemark::class.java)
            folderName?.let {
                query.equalTo(Field.folderName, it)
            }
            maxDistance?.let {
                query.lessThanOrEqualTo(Field.distance, it)
            }
            if (positions.isNotEmpty()) {
                query.`in`(Field.coordinate, positions.toTypedArray())
            }
            return query
        }

        object Field {

            val name = "name"

            val description = "description"

            val folderName = "folderName"

            val coordinate = "coordinate"

            val distance = "distance"

            val realmImageUrls = "realmImageUrls"

            val styleUrl = "styleUrl"
        }
    }
}