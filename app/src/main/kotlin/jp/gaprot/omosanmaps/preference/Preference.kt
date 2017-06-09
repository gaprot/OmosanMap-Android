/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.map.toCsv

enum class Preference(val key: String) {
    WALKING_SPEED(PreferenceManager.Key.WALKING_SPEED),
    KMZ_URL(PreferenceManager.Key.KMZ_URL),
    ORIGIN(PreferenceManager.Key.ORIGIN);

    fun getValue(context: Context): Any {
        when (this) {
            ORIGIN -> return PreferenceManager.getOrigin(context)
            KMZ_URL -> return PreferenceManager.getKmzUrl(context)
            WALKING_SPEED -> return PreferenceManager.getWalkingSpeed(context)
        }
    }

    fun prettyKey(context: Context): String {
        when (this) {
            WALKING_SPEED -> {
                return context.getString(R.string.pref_pretty_key_speed)
            }
            KMZ_URL -> {
                return context.getString(R.string.pref_pretty_key_kmz_url)
            }
            ORIGIN -> {
                return context.getString(R.string.pref_pretty_key_origin)
            }
        }
    }

    fun prettyValue(context: Context): String {
        val value = getValue(context)
        when (this) {
            WALKING_SPEED -> {
                return context.getString(R.string.pref_walking_speed_with_unit, value as Int)
            }
            KMZ_URL -> {
                return value as String
            }
            ORIGIN -> {
                return (value as LatLng).toCsv()
            }
        }
    }
}