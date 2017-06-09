/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.placemark

import android.text.TextUtils
import android.widget.TextView
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.Repository
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.extension.*
import jp.gaprot.omosanmaps.widget.ActivatableIconView
import jp.gaprot.omosanmaps.widget.activatableIconView
import jp.gaprot.omosanmaps.widget.placemarkHeaderView
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView

class PlaceDetailFragmentUI(

        private val placemark: Placemark,

        private val onSaveIconClick: (ActivatableIconView) -> Unit,

        private val onDirectionIconClick: (ActivatableIconView) -> Unit

) : AnkoComponent<PlaceDetailFragment> {

    override fun createView(ui: AnkoContext<PlaceDetailFragment>) = ui.apply {

        nestedScrollView {
            lparams(width = matchParent, height = matchParent)

            verticalLayout {
                // Header
                placemarkHeaderView {
                    backgroundColor = context.color(R.color.colorPrimary)
                    bind(placemark)
                }.lparams {
                    width = matchParent
                    height = wrapContent
                }

                // Description
                textView {
                    textSizeDimen = R.dimen.text_size_subhead
                    textColor = context.color(R.color.text_black)
                    padding = dimen(R.dimen.element_spacing_x2)
                }.lparams {
                    width = matchParent
                    height = wrapContent
                    minimumHeight = dimen(R.dimen.element_spacing_x4)
                }.let {
                    setDescription(it)
                }

                // Divider
                view {
                    backgroundResource = android.R.drawable.divider_horizontal_bright
                }.lparams(width = matchParent, height = dip(1))

                relativeLayout {
                    padding = dimen(R.dimen.element_spacing_x2)

                    // Location icon
                    imageView(R.drawable.ic_location_on_indigo_500_24dp) {
                        id = R.id.detail_location_icon
                    }.lparams {
                        centerVertically()
                    }

                    // Address
                    textView {
                        textSizeDimen = R.dimen.text_size_body
                        textColor = context.color(R.color.text_black)
                        maxLines = 2
                        ellipsize = TextUtils.TruncateAt.END
                    }.lparams {
                        width = matchParent
                        height = wrapContent
                        horizontalMargin = dimen(R.dimen.element_spacing_x1)
                        minimumHeight = dimen(R.dimen.list_item_min_height)
                        centerVertically()
                        rightOf(R.id.detail_location_icon)
                    }.let {
                        setAddress(it)
                    }

                }.lparams(width = matchParent, height = wrapContent)

                // Divider
                view {
                    backgroundResource = android.R.drawable.divider_horizontal_bright
                }.lparams(width = matchParent, height = dip(1))

                linearLayout {

                    // Save icon
                    activatableIconView {
                        iconDrawable = context.drawable(R.drawable.bg_star)
                        hint = context.getString(R.string.detail_hint_save)
                        hintColor = context.colorStateList(R.drawable.text_color_hint)
                        activate(placemark.starred)
                        onClick { onSaveIconClick(it as ActivatableIconView) }
                    }.lparams {
                        width = matchParent
                        height = wrapContent
                        weight = 1f
                    }

                    // Navigate icon
                    activatableIconView {
                        iconDrawable = context.drawable(R.drawable.ic_directions_indigo_500_36dp)
                        hint = context.getString(R.string.detail_hint_direction)
                        hintColor = context.colorStateList(R.drawable.text_color_hint)
                        onClick { onDirectionIconClick(it as ActivatableIconView) }
                    }.lparams {
                        width = matchParent
                        height = wrapContent
                        weight = 1f
                    }

                }.lparams {
                    width = matchParent
                    height = wrapContent
                }

            }.lparams(width = matchParent, height = matchParent)
        }
    }.view

    private fun setAddress(addressText: TextView) {
        placemark.getAddress(addressText.context)
                .async()
                .onSuccess { address ->
                    addressText.text = address.getAddressLine(1)
                }
                .onError { addressText.setText(R.string.detail_no_address) }
                .subscribe()
    }

    private fun setDescription(descriptionText: TextView) {
        val description = placemark.prettyDescription()
        if (description.isNullOrEmpty()) {
            descriptionText.setText(R.string.detail_no_description)
        } else {
            descriptionText.text = description
        }
    }
}