/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model

import io.realm.RealmObject
import jp.gaprot.omosanmaps.data.model.abst.Convertible

open class RealmString(

        open var value: String = ""

) : RealmObject(), Convertible<String> {

    override fun convert(): String {
        return value
    }

}
