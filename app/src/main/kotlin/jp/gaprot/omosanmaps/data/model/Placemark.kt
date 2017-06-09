/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.model

import android.annotation.TargetApi
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Parcelable
import android.text.Html
import android.text.Spanned
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.realm.annotations.Ignore
import jp.gaprot.omosanmaps.data.model.abst.Entity
import jp.gaprot.omosanmaps.data.model.abst.Unwrappable
import jp.gaprot.omosanmaps.data.model.abst.Wrappable
import jp.gaprot.omosanmaps.map.distance
import jp.gaprot.omosanmaps.map.toCsv
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import rx.Single
import java.io.IOException
import java.util.*

@PaperParcel
data class Placemark(

        override val name: String,

        val description: String,

        val folderName: String,

        val latitude: Double,

        val longitude: Double,

        val distance: Int,

        val imageUrls: List<String>,

        val styleUrl: String,

        var starred: Boolean = false

) : Entity, PaperParcelable, Wrappable {

    companion object {

        val TAG: String = Placemark::class.java.simpleName

        @JvmField val CREATOR = PaperParcelable.Creator(Placemark::class.java)
    }

    override fun wrap(): Parcelable {
        return PlacemarkParcel(this)
    }

    object Unwrapper : Unwrappable<Placemark>

    /**
     * Non initial variable needs `@Transient` annotation for Paperparcel.
     * Because of kapt bug.
     */
    @Ignore
    @Transient
    var latlng = LatLng(latitude, longitude)

    @Transient
    var coordinate = latlng.toCsv()

    @Ignore
    @Transient
    var markerOptions: MarkerOptions = MarkerOptions().position(latlng).title(name)

    fun distance(from: LatLng): Int {
        val distance = latlng.distance(from).toDouble()
        return Math.ceil(distance).toInt()
    }

    fun getAddress(context: Context): Single<Address> {
        return Single.create {
            val geoCoder = Geocoder(context, Locale.getDefault())
            try {
                val result = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
                if (result.isEmpty()) {
                    it.onError(Throwable("No result"))
                    return@create
                }
                it.onSuccess(result[0])
            } catch (e: IOException) {
                Log.e(TAG, "address: ", e)
                it.onError(e)
            }
        }
    }

    @Suppress("deprecation")
    @TargetApi(Build.VERSION_CODES.N)
    fun prettyDescription(): Spanned? {
        val description = removeImageTags(description)
        if (description.isEmpty()) {
            return null
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(description)
        }
        return Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)
    }

    private fun removeImageTags(source: String): String {
        var removed = source
        for (url in imageUrls) {
            val targetIndex = removed.indexOf("<img src=")
            val targetLastIndex = removed.indexOf("/>", targetIndex) + 2
            val target = removed.substring(targetIndex, targetLastIndex)
            removed = removed.replace(target, "")
        }
        return removed
    }

    class Builder {

        private var name = ""

        private var description = ""

        private var folderName = ""

        private var latitude = 0.0

        private var longitude = 0.0

        private var distance = 0

        private var imageUrls: List<String> = ArrayList()

        private var styleUrl = ""

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun setFolderName(folderName: String): Builder {
            this.folderName = folderName
            return this
        }

        fun setLatLng(latlng: LatLng): Builder {
            latitude = latlng.latitude
            longitude = latlng.longitude
            return this
        }

        fun setDistance(distance: Int): Builder {
            this.distance = distance
            return this
        }

        fun setImageUrls(imageUrls: List<String>): Builder {
            this.imageUrls = imageUrls
            return this
        }

        fun setStyleUrl(styleUrl: String): Builder {
            this.styleUrl = styleUrl
            return this
        }

        fun build(): Placemark {
            return Placemark(
                    name = name,
                    description = description,
                    folderName = folderName,
                    latitude = latitude,
                    longitude = longitude,
                    distance = distance,
                    imageUrls = imageUrls,
                    styleUrl = styleUrl
            )
        }

        fun build(origin: LatLng? = null): Placemark {
            origin?.let {
                distance = it.distance(LatLng(latitude, longitude)).toInt()
            }
            return Placemark(
                    name = name,
                    description = description,
                    folderName = folderName,
                    latitude = latitude,
                    longitude = longitude,
                    distance = distance,
                    imageUrls = imageUrls,
                    styleUrl = styleUrl
            )
        }
    }
}