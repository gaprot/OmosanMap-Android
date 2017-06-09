/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.map

import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import jp.gaprot.omosanmaps.data.model.Placemark

fun GoogleMap.zoomTo(marker: Marker) {
    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, MapConstants.ZOOM_LEVEL)
    animateCamera(cameraUpdate, MapConstants.DURATION, null)
}

fun GoogleMap.addMarker(placemark: Placemark, icon: BitmapDescriptor?): Marker {
    Log.d(GoogleMap::class.java.simpleName, "addMarker: $placemark")
    val options = placemark.markerOptions
    if (icon == null) {
        return addMarker(options)
    }
    return addMarker(options.icon(icon))
}

fun GoogleMap.addMarker(latlng: LatLng, icon: BitmapDescriptor?): Marker {
    val options = MarkerOptions().position(latlng)
    if (icon == null) {
        return addMarker(options)
    }
    return addMarker(options.icon(icon))
}