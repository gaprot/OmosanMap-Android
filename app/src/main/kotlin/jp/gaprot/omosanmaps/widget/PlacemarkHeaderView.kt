/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.widget

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.RelativeLayout
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.preference.PreferenceManager
import org.jetbrains.anko.dimen
import org.jetbrains.anko.find
import org.jetbrains.anko.padding

class PlacemarkHeaderView(context: Context) : RelativeLayout(context) {

    private val titleText: AppCompatTextView

    private val categoryText: AppCompatTextView

    private val walkIcon: AppCompatImageView

    private val travelTimeText: AppCompatTextView

    init {
        View.inflate(context, R.layout.view_placemark_header, this)

        titleText = find<AppCompatTextView>(R.id.title_text)
        categoryText = find<AppCompatTextView>(R.id.category_text)
        walkIcon = find<AppCompatImageView>(R.id.walk_icon)
        travelTimeText = find<AppCompatTextView>(R.id.travel_time_text)

        padding = dimen(R.dimen.element_spacing_x2)
    }

    fun bind(placemark: Placemark) {
        titleText.text = placemark.name
        categoryText.text = placemark.folderName
        val distance = placemark.distance(PreferenceManager.getOrigin(context))
        val travelTime = distance / PreferenceManager.getWalkingSpeed(context)
        travelTimeText.text = context.getString(R.string.travel_time_with_min, travelTime)
    }
}
