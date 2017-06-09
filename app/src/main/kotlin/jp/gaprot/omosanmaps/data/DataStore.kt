/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data

import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Placemark
import rx.Single

/**
 *  データ取得に加えて、保存もできる interface
 */
interface DataStore : DataServer {

    fun saveDocument(document: Document): Single<Unit>

    fun saveStarredPosition(position: String): Single<Unit>

    fun getPlacemarksWithFilter(filter: Filter): Single<List<Placemark>>

    fun getStarredPositions(): Single<List<String>>

    fun removeStarredPosition(position: String): Single<Unit>
}