/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data

import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import rx.Single

/**
 *  もっぱらデータ取得のためだけの interface
 */
interface DataServer {

    fun getDocument(): Single<Document>

    fun getFolders(): Single<List<Folder>>

    fun getPlacemarks(): Single<List<Placemark>>

    fun getPlacemarksByPosition(positions: List<String>): Single<List<Placemark>>
}