/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.map

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun LatLng.toCsv(): String {
    return latitude.toString() + "," + longitude.toString()
}

fun LatLng.distance(from: LatLng): Float {
    val result = FloatArray(3)
    Location.distanceBetween(from.latitude, from.longitude, latitude, longitude, result)
    return result[0]
}

object LatLngFactory {

    fun from(csv: String): LatLng {
        val coordinate = csv.split(",")
        return LatLng(coordinate[0].toDouble(), coordinate[1].toDouble())
    }

    fun from(latitudeStr: String, longitudeStr: String): LatLng {
        return LatLng(latitudeStr.toDouble(), longitudeStr.toDouble())
    }
}