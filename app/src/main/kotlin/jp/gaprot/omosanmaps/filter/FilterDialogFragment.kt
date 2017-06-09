/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.filter

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.Repository
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.eventbus.NewFilterEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import jp.gaprot.omosanmaps.extension.async
import jp.gaprot.omosanmaps.extension.onSuccess
import jp.gaprot.omosanmaps.preference.PreferenceManager
import org.jetbrains.anko.find
import rx.subscriptions.CompositeSubscription

class FilterDialogFragment : DialogFragment() {

    companion object {
        private val TAG: String = FilterDialogFragment::class.java.simpleName

        @JvmStatic fun newInstance(filter: Filter): FilterDialogFragment {
            return FilterDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Filter::class.java.simpleName, filter.wrap())
                }
            }
        }
    }

    private lateinit var currentFilter: Filter

    private val walkingSpeed: Int by lazy {
        PreferenceManager.getWalkingSpeed(context)
    }

    private var folderNames: List<String> = emptyList()

    private val travelTimes: IntArray by lazy {
        resources.getIntArray(R.array.pref_travel_time_array)
    }

    private val travelTimesString: List<String> by lazy {
        travelTimes.map {
            if (it == 0) {
                return@map getString(R.string.blank)
            }
            return@map getString(R.string.travel_time_with_min, it)
        }
    }

    private lateinit var folderNameSpinner: Spinner

    private lateinit var travelTimeSpinner: Spinner

    private lateinit var starredCheckbox: CheckBox

    private val subscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentFilter = Filter.Unwrapper.unwrap(arguments, Filter::class.java.simpleName)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val root = LayoutInflater.from(context).inflate(R.layout.dialog_filter, null).apply {
            folderNameSpinner = find(R.id.folder_name_spinner)
            travelTimeSpinner = find(R.id.distance_spinner)
            starredCheckbox = find(R.id.starred_checkbox)
        }

        return AlertDialog.Builder(context).apply {
            setTitle(R.string.filter_label)
            setView(root)
            setPositiveButton(R.string.action_search, onPositiveButtonClick)
        }.create()
    }

    private fun getFolderNames() {
        subscription.add(Repository.getInstance(context)
                .getFolders()
                .async()
                .onSuccess { folders ->
                    val names = folders.map { it.name }.toMutableList()

                    // 未選択時のための「---」を挿入
                    names.add(0, context.getString(R.string.blank))
                    folderNames = names
                    setupSpinners(names)
                }
                .onError { Log.e(TAG, "getFolderNames: ", it) }
                .subscribe())
    }

    private fun setupSpinners(folderNames: List<String>) {
        folderNameSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, folderNames)
        travelTimeSpinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, travelTimesString.toMutableList())
        bindFilter()
    }

    /**
     *  currentFilter を UI に反映
     */
    private fun bindFilter() {
        folderNames.indexOf(currentFilter.folderName).let {
            if (it.isPositive()) {
                folderNameSpinner.setSelection(it)
            }
        }
        currentFilter.maxTravelTime(walkingSpeed)?.let {
            travelTimes.indexOf(it).let {
                if (it.isPositive()) {
                    travelTimeSpinner.setSelection(it)
                }
            }
        }
        starredCheckbox.isChecked = currentFilter.starred
    }

    private val onPositiveButtonClick: (DialogInterface?, Int) -> Unit = { dialog, index ->
        val folderNameIndex = folderNameSpinner.selectedItemPosition
        val travelTimeIndex = travelTimeSpinner.selectedItemPosition

        val builder = Filter.Builder()
        if (folderNameIndex != 0) {
            builder.setFolderName(folderNames[folderNameIndex])
        }
        if (travelTimeIndex != 0) {
            val travelTime = travelTimes[travelTimeIndex]
            builder.setMaxDistance(travelTime * walkingSpeed)
        }
        builder.setStarred(starredCheckbox.isChecked)
        val filter = builder.build()
        RxBus.post(NewFilterEvent(filter))
    }

    override fun onResume() {
        super.onResume()
        if (folderNames.isEmpty()) {
            getFolderNames()
        }
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}

fun Filter.maxTravelTime(speed: Int): Int? {
    maxDistance?.let { return it / speed }
    return null
}

fun Int.isPositive(): Boolean {
    return this >= 0
}