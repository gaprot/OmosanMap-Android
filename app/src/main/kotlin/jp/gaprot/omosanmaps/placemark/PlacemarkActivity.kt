/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.placemark

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.maps.model.Marker
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.Repository
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.eventbus.*
import jp.gaprot.omosanmaps.extension.async
import jp.gaprot.omosanmaps.extension.onSuccess
import jp.gaprot.omosanmaps.filter.FilterDialogFragment
import jp.gaprot.omosanmaps.map.MapManager
import jp.gaprot.omosanmaps.preference.PreferenceManager
import org.jetbrains.anko.setContentView
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class PlacemarkActivity : AppCompatActivity() {

    companion object {

        private val TAG: String = PlacemarkActivity::class.java.simpleName

        @JvmStatic
        fun createIntent(cxt: Context, filter: Filter): Intent {
            return Intent(cxt, PlacemarkActivity::class.java).apply {
                putExtra(Filter::class.java.simpleName, filter.wrap())
            }
        }
    }

    private var filter: Filter = Filter()

    private var placemarks: List<Placemark> = emptyList()
        set(value) {
            Log.d(TAG, "data set!")
            mapManager.placemarks = value
            field = value
        }

    private val ui: PlacemarkActivityUI by lazy {
        PlacemarkActivityUI(mapManager, onSort, onFilter)
    }

    private val onSort: () -> Unit = {
        placemarks = placemarks.sortedBy { it.distance(PreferenceManager.getOrigin(this)) }
        RxBus.post(NewSortEvent(placemarks))
    }

    private val onFilter: () -> Unit = {
        FilterDialogFragment.newInstance(filter)
                .show(supportFragmentManager, FilterDialogFragment::class.java.simpleName)
    }

    private val bottomSheetBehavior by lazy {
        ui.bottomSheetBehavior
    }

    private val mapManager: MapManager by lazy { MapManager(this) }

    private val subscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        mapManager.mapView = ui.mapView

        filter = Filter.Unwrapper.unwrap(intent, Filter::class.java.simpleName)
        if (savedInstanceState == null) {
            showList(filter)
        }
    }

    private fun getPlacemarkWithFilter(filter: Filter) {
        Repository.getInstance(this)
                .getPlacemarksWithFilter(filter)
                .async()
                .onSuccess { placemarks = it }
                .onError { Log.e(TAG, "getPlacemarkWithFilter: ", it) }
                .subscribe()
    }

    private fun onPlacemarkSelectedEvent(placemark: Placemark) {
        Log.d(TAG, "onPlacemarkSelectedEvent: Catch!")
        mapManager.onPlacemarkSelected(placemark)
        showDetail(placemark)
    }

    private fun showList(filter: Filter) {
        ui.bottomActionBar.visibility = View.VISIBLE
        getPlacemarkWithFilter(filter)
        supportFragmentManager.beginTransaction()
                .replace(ui.bottomSheet.id,
                        PlaceListFragment.newInstance(filter), PlaceListFragment::class.java.simpleName)
                .commit()
    }

    private fun showDetail(placemark: Placemark) {
        ui.bottomActionBar.visibility = View.GONE
        supportFragmentManager.beginTransaction()
                .replace(R.id.bottom_sheet_container,
                        PlaceDetailFragment.newInstance(placemark), PlaceDetailFragment::class.java.simpleName)
                .commit()
    }

    private var isDetailVisible: Boolean = false
        get() {
            val detail = supportFragmentManager.findFragmentByTag(PlaceDetailFragment::class.java.simpleName)
            return detail?.isVisible ?: false
        }

    fun onMarkerClick(marker: Marker) {
        val selected = placemarks.filter {
            it.name == marker.title
        }
        if (selected.isEmpty()) {
            Log.e(TAG, "onMarkerClick: ", Throwable("No correspond placemark to $marker"))
            return
        }
        showDetail(selected.first())
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        }

        if (isDetailVisible) {
            mapManager.updateCameraWithMarkers()
            showList(filter)
            return
        }
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        subscription.add(RxBus.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is PlacemarkSelectedEvent -> onPlacemarkSelectedEvent(it.placemark)
                        is NewFilterEvent -> {
                            filter = it.filter
                            showList(it.filter)
                        }
                        is MarkerClickEvent -> onMarkerClick(it.marker)
                    }
                }
        )
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}
