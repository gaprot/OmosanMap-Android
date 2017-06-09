/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.extension.selectableItemBackground
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.find

class ActivatableIconView(context: Context) : RelativeLayout(context) {

    var iconDrawable: Drawable? = null
        set(value) {
            if (value != null) {
                iconView.background = value
            }
        }

    var hint: String? = null
        set(value) {
            if (value != null) {
                hintTextView.text = value
            }
        }

    var hintColor: ColorStateList? = null
        set(value) {
            if (value != null) {
                hintTextView.setTextColor(value)
            }
        }

    private val iconView: AppCompatImageView

    private val loadingView: ProgressBar

    private val hintTextView: AppCompatTextView

    init {
        View.inflate(context, R.layout.view_activatable_icon, this)

        iconView = find<AppCompatImageView>(R.id.icon_view)
        loadingView = find<ProgressBar>(R.id.progressbar)
        hintTextView = find<AppCompatTextView>(R.id.hint_text)

        backgroundDrawable = context.selectableItemBackground
    }

    fun activate(active: Boolean) {
        isActivated = active
        iconView.isActivated = active
        hintTextView.isActivated = active
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            iconView.visibility = View.INVISIBLE
            loadingView.visibility = View.VISIBLE
        } else {
            iconView.visibility = View.VISIBLE
            loadingView.visibility = View.INVISIBLE
        }
    }
}