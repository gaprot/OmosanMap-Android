/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.maps.model.LatLng
import jp.gaprot.omosanmaps.extension.edit
import jp.gaprot.omosanmaps.map.LatLngFactory
import jp.gaprot.omosanmaps.map.toCsv

/**
 *  設定値を SharedPreferences に保存
 */
object PreferenceManager {

    private fun default(context: Context): SharedPreferences {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getOrigin(context: Context): LatLng {
        val sp = default(context)
        val csv = sp.getString(Key.ORIGIN, Default.ORIGIN)
        return LatLngFactory.from(csv)
    }

    fun getKmzUrl(context: Context): String {
        val sp = default(context)
        return sp.getString(Key.KMZ_URL, Default.KMZ_URL)
    }

    fun getWalkingSpeed(context: Context): Int {
        val sp = default(context)
        return sp.getInt(Key.WALKING_SPEED, Default.WALKING_SPEED)
    }

    fun updateOrigin(latlng: LatLng, context: Context) {
        updateOrigin(latlng.latitude, latlng.longitude, context)
    }

    fun updateOrigin(latitude: Double, longitude: Double, context: Context) {
        default(context).edit {
            putString(Key.ORIGIN, LatLng(latitude, longitude).toCsv())
        }
    }

    fun updateKmzUrl(url: String, context: Context) {
        default(context).edit {
            putString(Key.KMZ_URL, url)
        }
    }

    fun updateWalkingSpeed(speed: Int, context: Context) {
        default(context).edit {
            putInt(Key.WALKING_SPEED, speed)
        }
    }

    object Key {
        val ORIGIN = "origin"
        val KMZ_URL = "kmzUrl"
        val WALKING_SPEED = "walkingSpeed"
    }

    private object Default {
        val ORIGIN = "35.6666991,139.7085862"
        val KMZ_URL = "https://www.google.com/maps/d/u/0/kml?mid=zmkzjAlquG4s.k9j-gW9GbmCQ"
        val WALKING_SPEED = 80
    }
}