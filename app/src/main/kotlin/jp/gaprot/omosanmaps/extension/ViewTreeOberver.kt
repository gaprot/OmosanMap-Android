/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.extension

import android.os.Build
import android.view.ViewTreeObserver

/**
 *  もっぱら compatibility のためのヘルパ
 */
@Suppress("deprecation")
fun ViewTreeObserver.removeOnGlobalLayoutListenerCompat(target: ViewTreeObserver.OnGlobalLayoutListener) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
        removeGlobalOnLayoutListener(target)
    } else {
        removeOnGlobalLayoutListener(target)
    }
}