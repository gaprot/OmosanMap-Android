/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import jp.gaprot.omosanmaps.data.model.abst.Convertible

open class RealmStarred(

        @PrimaryKey
        var position: String = ""

) : RealmObject(), Convertible<String> {

    object Field {

        val position = "position"
    }

    override fun convert(): String {
        return position
    }
}