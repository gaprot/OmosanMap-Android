/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.abst.Convertible
import jp.gaprot.omosanmaps.data.store.model.util.convert
import jp.gaprot.omosanmaps.data.store.model.util.realmPlacemarks

open class RealmFolder(

        @PrimaryKey
        var name: String = "",

        var realmPlacemarks: RealmList<RealmPlacemark> = RealmList()

) : RealmObject(), Convertible<Folder> {

    constructor(folder: Folder) : this(
            name = folder.name,
            realmPlacemarks = folder.realmPlacemarks()
    )

    override fun convert(): Folder {
        return Folder.Builder()
                .setName(name)
                .setPlacemarks(realmPlacemarks.convert().toMutableList())
                .build()
    }
}