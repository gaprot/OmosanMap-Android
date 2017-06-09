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
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.extension.async
import jp.gaprot.omosanmaps.extension.onSuccess
import jp.gaprot.omosanmaps.map.MapIntentBuilder
import jp.gaprot.omosanmaps.widget.ActivatableIconView
import org.jetbrains.anko.AnkoContext
import rx.subscriptions.CompositeSubscription

class PlaceDetailFragment : Fragment() {

    companion object {

        private val TAG: String = PlaceDetailFragment::class.java.simpleName

        @JvmStatic fun newInstance(placemark: Placemark): PlaceDetailFragment {
            return PlaceDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Placemark::class.java.simpleName, placemark.wrap())
                }
            }
        }
    }

    private lateinit var placemark: Placemark

    private val ui: PlaceDetailFragmentUI by lazy {
        PlaceDetailFragmentUI(
                placemark = placemark,
                onSaveIconClick = { onSaveIconClick(it) },
                onDirectionIconClick = { onDirectionIconClick(it) }
        )
    }

    private val subscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Placemark.Unwrapper.unwrap(arguments, Placemark::class.java.simpleName).let {
            val isStarred = Repository.getInstance(context)
                    .isStarred(it.coordinate)
            placemark = it.copy(starred = isStarred)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = ui.createView(AnkoContext.create(context, this))
        return root
    }

    private fun onSaveIconClick(saveIcon: ActivatableIconView) {
        saveIcon.setLoading(true)
        subscription.add(Repository.getInstance(context)
                .toggleStarred(placemark.coordinate)
                .async()
                .onSuccess {
                    saveIcon.activate(!saveIcon.isActivated)
                    saveIcon.setLoading(false)
                }
                .onError { Log.e(TAG, "onSaveIconClick: ", it) }
                .subscribe())
    }

    private fun onDirectionIconClick(navigateIcon: ActivatableIconView) {
        MapIntentBuilder(placemark).create(context).let {
            startActivity(it)
        }
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}