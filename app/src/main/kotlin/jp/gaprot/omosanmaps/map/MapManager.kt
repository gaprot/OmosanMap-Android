/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.map

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.eventbus.MarkerClickEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import jp.gaprot.omosanmaps.extension.removeOnGlobalLayoutListenerCompat
import jp.gaprot.omosanmaps.preference.PreferenceManager
import java.util.*

object MapConstants {
    val PADDING = 100
    val ZOOM_LEVEL = 18f
    val DURATION = 400
}

class MapManager(

        private val context: Context

) : OnMapReadyCallback {

    companion object {
        private val TAG: String = MapManager::class.java.simpleName
    }

    lateinit var mapView: View

    var placemarks: List<Placemark> = emptyList()
        set(value) {
            Log.d(TAG, "placemark is set! $value")
            if (mapReady) {
                setupMarkers(value)
            }
            field = value
        }

    private lateinit var map: GoogleMap

    private var mapReady: Boolean = false

    private var markers: MutableMap<LatLng, Marker> = HashMap()

    private var lastClickedMarker: Marker? = null

    private var iconNormal: BitmapDescriptor? = null

    private var iconOnSelected: BitmapDescriptor? = null

    private var iconOrigin: BitmapDescriptor? = null

    override fun onMapReady(map: GoogleMap?) {
        if (map == null) {
            Log.e(TAG, "onMapReady: ", NullPointerException("Google map is null"))
            return
        }
        mapReady = true
        this.map = map
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(PreferenceManager.getOrigin(context), 16f))

        initMarkerIcon()

        if (placemarks.isNotEmpty()) {
            setupMarkers(placemarks)
        }
        map.setOnMarkerClickListener { onMarkerClick(it) }
    }

    // GoogleMap が取得されてからじゃないと Bitmap を作成できない
    private fun initMarkerIcon() {
        iconNormal = MarkerIcon.restaurant(context)
        iconOnSelected = MarkerIcon.default()
        iconOrigin = MarkerIcon.origin()
    }

    // アイコンを変えてズーム、イベントバスで通知
    private fun onMarkerClick(marker: Marker): Boolean {
        // Origin なら何もしない
        if (marker.position == PreferenceManager.getOrigin(context)) {
            return false
        }
        lastClickedMarker?.let { lastMarker ->
            if (marker == lastMarker) {
                Log.d(TAG, "onMarkerClick: Same marker clicked!")
                return@let
            }
            lastMarker.setIcon(iconNormal)
        }
        lastClickedMarker = marker
        marker.setIcon(iconOnSelected)
        map.zoomTo(marker)
        RxBus.post(MarkerClickEvent(marker))
        return false
    }

    // Marker 配置して画面内に全てのマーカが収まるようにする
    private fun setupMarkers(placemarks: List<Placemark>) {
        Log.d(TAG, "setupMarkers: ")
        removeMarkers()
        populateOriginMarker()
        populateMarkers(placemarks)
        addViewTreeObserverForUpdateCamera()
    }

    private fun populateOriginMarker() {
        val origin = PreferenceManager.getOrigin(context)
        val originMarker = map.addMarker(origin, iconOrigin)
        markers[origin] = originMarker
    }

    private fun populateMarkers(placemarks: List<Placemark>) {
        Log.d(TAG, "populateMarkers: ")
        for (placemark in placemarks) {
            val marker = map.addMarker(placemark, iconNormal)
            Log.d(TAG, "populateMarkers: " + placemark.name)
            markers[placemark.latlng] = marker
        }
    }

    private fun removeMarkers() {
        lastClickedMarker = null
        if (markers.isEmpty()) return
        for (marker in markers.values) {
            marker.remove()
        }
        markers.clear()
        map.clear()
    }

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    // View のサイズが定まってからでないと、うまく全てを収めたズームレベルを決められない
    private fun addViewTreeObserverForUpdateCamera() {
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            Log.d(TAG, "addViewTreeObserverForUpdateCamera: OnSizeFix")
            updateCameraWithMarkers()
            globalLayoutListener?.let {
                mapView.viewTreeObserver.removeOnGlobalLayoutListenerCompat(it)
            }
        }
        mapView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    fun updateCameraWithMarkers() {
        val builder = LatLngBounds.Builder()
        markers.keys.forEach { builder.include(it) }

        builder.include(PreferenceManager.getOrigin(context))
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), MapConstants.PADDING)
        map.animateCamera(cameraUpdate)
    }

    fun onPlacemarkSelected(placemark: Placemark) {
        val marker = markers[placemark.latlng]
        if (marker == null) {
            Log.e(TAG, "onPlacemarkSelected: No marker corresponds to ${placemark.name}")
            return
        }
        marker.showInfoWindow()
        onMarkerClick(marker)
    }
}