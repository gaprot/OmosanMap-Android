/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model.util

import io.realm.RealmList
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.data.model.abst.Convertible
import jp.gaprot.omosanmaps.data.store.model.RealmFolder
import jp.gaprot.omosanmaps.data.store.model.RealmPlacemark
import jp.gaprot.omosanmaps.data.store.model.RealmString

fun <T, C : Convertible<T>> List<C>.convert(): List<T> {
    return map { it.convert() }
}

fun Placemark.realmImageUrls(): RealmList<RealmString> {
    val buffer = RealmList<RealmString>()
    imageUrls.mapTo(buffer, ::RealmString)
    return buffer
}

fun Folder.realmPlacemarks(): RealmList<RealmPlacemark> {
    val buffer = RealmList<RealmPlacemark>()
    placemarks.mapTo(buffer, ::RealmPlacemark)
    return buffer
}

fun Document.realmFolders(): RealmList<RealmFolder> {
    val buffer = RealmList<RealmFolder>()
    folders.mapTo(buffer, ::RealmFolder)
    return buffer
}