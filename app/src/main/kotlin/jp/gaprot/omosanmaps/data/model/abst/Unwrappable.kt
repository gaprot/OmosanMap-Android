/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.model.abst

import android.content.Intent
import android.os.Bundle
import nz.bradcampbell.paperparcel.PaperParcelable
import nz.bradcampbell.paperparcel.PaperParcels
import nz.bradcampbell.paperparcel.TypedParcelable

interface Unwrappable<out T : PaperParcelable> {

    fun unwrap(intent: Intent, key: String): T {
        return PaperParcels.unwrap(intent.getParcelableExtra<TypedParcelable<T>>(key))
    }

    fun unwrap(bundle: Bundle, key: String): T {
        return PaperParcels.unwrap(bundle.getParcelable<TypedParcelable<T>>(key))
    }
}