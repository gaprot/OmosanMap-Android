/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import jp.gaprot.omosanmaps.data.model.Placemark

class MapIntentBuilder(placemark: Placemark) {

    companion object {
        private val TAG: String = MapIntentBuilder::class.java.simpleName
    }

    private val mapPackageName = "com.google.android.apps.maps"

    val uri: Uri

    init {
        val latlng = placemark.latlng
        uri = Uri.parse("geo:${latlng.toCsv()}?q=${Uri.encode(placemark.name)}")
    }

    fun create(context: Context): Intent? {
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.`package` = mapPackageName
        if (mapIntent.resolveActivity(context.packageManager) == null) {
            Log.e(TAG, "create: We don't have google maps app!")
            return null
        }
        return mapIntent
    }

}