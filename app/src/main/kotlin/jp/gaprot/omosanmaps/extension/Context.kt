/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.extension

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.AttrRes
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue

val Context.selectableItemBackground: Drawable get() {
    val typedArray = obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
    val selectableItemBackground = typedArray.getDrawable(0)
    typedArray.recycle()
    return selectableItemBackground
}

val Context.actionBarSize: Int get() {
    return attrToDimen(android.R.attr.actionBarSize)
}

fun Context.attribute(@AttrRes resId: Int): TypedValue {
    val tv = TypedValue()
    theme.resolveAttribute(resId, tv, true)
    return tv
}

fun Context.attrToDimen(@AttrRes resId: Int): Int {
    return TypedValue.complexToDimensionPixelSize(
            attribute(resId).data, resources.displayMetrics
    )
}

fun Context.drawable(@DrawableRes resId: Int): Drawable {
    return ContextCompat.getDrawable(this, resId)
}

fun Context.color(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.colorStateList(@DrawableRes resId: Int): ColorStateList {
    return ContextCompat.getColorStateList(this, resId)
}