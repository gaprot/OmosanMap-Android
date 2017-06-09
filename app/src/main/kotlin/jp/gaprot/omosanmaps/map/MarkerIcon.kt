/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.map

import android.content.Context
import android.support.annotation.DrawableRes
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.extension.drawable
import jp.gaprot.omosanmaps.extension.toBitmap

// GoogleMap が取得されてからじゃないと BitmapDescriptorFactory を使えないので注意（全部 null を返してくる）
object MarkerIcon {

    fun default(): BitmapDescriptor? {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) ?: null
    }

    fun origin(): BitmapDescriptor? {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) ?: null
    }

    fun restaurant(context: Context): BitmapDescriptor? {
        return fromDrawable(context, R.drawable.ic_restaurant)
    }

    fun fromDrawable(context: Context, @DrawableRes resId: Int): BitmapDescriptor? {
        val drawable = context.drawable(resId)
        val bitmap = drawable.toBitmap() ?: return null
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}