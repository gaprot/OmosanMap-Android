/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.placemark

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.gaprot.omosanmaps.data.Repository
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.eventbus.NewSortEvent
import jp.gaprot.omosanmaps.eventbus.PlacemarkSelectedEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import jp.gaprot.omosanmaps.extension.async
import jp.gaprot.omosanmaps.extension.onSuccess
import jp.gaprot.omosanmaps.widget.recycler.EntityAdapter
import org.jetbrains.anko.AnkoContext
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class PlaceListFragment : Fragment() {

    companion object {
        private val TAG: String = PlaceListFragment::class.java.simpleName

        @JvmStatic fun newInstance(filter: Filter): PlaceListFragment {
            return PlaceListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Filter::class.java.simpleName, filter.wrap())
                }
            }
        }
    }

    private lateinit var filter: Filter

    private var placemarks: List<Placemark> = emptyList()
        set(value) {
            ui.setEmptyView(value.isEmpty())
            placemarkAdapter.setData(value)
            field = value
        }

    private val placemarkAdapter: EntityAdapter<Placemark>

    init {
        placemarkAdapter = EntityAdapter(placemarks).apply {
            setOnItemClickListener { onPlacemarkClick(it) }
        }
    }

    private val subscription = CompositeSubscription()

    private val ui: PlaceListFragmentUI by lazy {
        PlaceListFragmentUI(
                placemarkAdapter = placemarkAdapter,
                onRetryButtonClick = { getPlacemarksWithFilter(filter) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filter = Filter.Unwrapper.unwrap(arguments, Filter::class.java.simpleName)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = ui.createView(AnkoContext.create(context, this))
        getPlacemarksWithFilter(filter)
        return root
    }

    private fun onPlacemarkClick(placemark: Placemark) {
        subscription.add(Repository.getInstance(context)
                .getStarredPositions()
                .async()
                .onSuccess {
                    placemark.starred = it.contains(placemark.coordinate)
                    RxBus.post(PlacemarkSelectedEvent(placemark))
                }
                .onError { Log.e(TAG, "onPlacemarkClick: ", it) }
                .subscribe())
    }

    private fun getPlacemarksWithFilter(filter: Filter) {
        subscription.add(Repository.getInstance(context)
                .getPlacemarksWithFilter(filter)
                .async()
                .doOnSubscribe { ui.setLoadingView(true) }
                .onSuccess {
                    placemarks = it
                    ui.setEmptyView(it.isEmpty())
                    ui.setLoadingView(false)
                }
                .onError { Log.e(TAG, "getPlacemarkWithFilter: ", it) }
                .subscribe())
    }

    override fun onResume() {
        super.onResume()
        subscription.add(
                RxBus.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            when (it) {
                                is NewSortEvent -> placemarks = it.placemarks
                            }
                        }
        )
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}