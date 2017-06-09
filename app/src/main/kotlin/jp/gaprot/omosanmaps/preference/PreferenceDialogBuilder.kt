/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.content.Context
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.gms.maps.model.LatLng
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.eventbus.PreferenceUpdateEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import jp.gaprot.omosanmaps.widget.textInputEditText
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputLayout

class PreferenceDialogBuilder(

        private val preference: Preference,

        private val context: Context

) {

    companion object {
        private val TAG: String = PreferenceDialogBuilder::class.java.simpleName
    }

    fun create(): AlertDialog {
        when (preference) {
            Preference.WALKING_SPEED -> return createSpeedDialog()
            Preference.KMZ_URL -> return createKmlUrlDialog()
            Preference.ORIGIN -> return createOriginDialog()
        }
    }

    private fun createSpeedDialog(): AlertDialog {
        val values = context.resources.getIntArray(R.array.pref_speed_list_values)
        val currentVal = PreferenceManager.getWalkingSpeed(context)
        val position = values.indexOf(currentVal)
        Log.d(TAG, "createSpeedDialog: $position")

        return AlertDialog.Builder(context).apply {
            var selected = currentVal
            setTitle(R.string.pref_speed_title)
            setSingleChoiceItems(R.array.pref_speed_list_titles, position, { dialog, position ->
                selected = values.find { it == values[position] } ?: error("Outta index")
            })
            setPositiveButton(R.string.dialog_ok, { dialog, i ->
                if (selected == currentVal) return@setPositiveButton
                PreferenceManager.updateWalkingSpeed(selected, context)
                RxBus.post(PreferenceUpdateEvent(preference.key))
            })
            setNegativeButton(R.string.dialog_cancel, null)
        }.create()
    }

    private fun createKmlUrlDialog(): AlertDialog {
        var urlEdit: EditText? = null
        val contentView: View = context.frameLayout {
            lparams(width = matchParent, height = wrapContent)
            padding = dimen(R.dimen.element_spacing_x2)

            textInputLayout {
                hint = context.getString(R.string.pref_kmz_url_hint)
                urlEdit = textInputEditText {
                    // Workaround for Anko issue (https://github.com/Kotlin/anko/issues/264)
                    layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setText(preference.getValue(context) as String)
                }
            }
        }

        return AlertDialog.Builder(context).apply {
            setView(contentView)
            setTitle(R.string.pref_kmz_url_title)
            setPositiveButton(R.string.dialog_ok, { dialog, i ->
                urlEdit?.text?.toString()?.let {
                    PreferenceManager.updateKmzUrl(it, context)
                    RxBus.post(PreferenceUpdateEvent(preference.key))
                }
            })
            setNegativeButton(R.string.dialog_cancel, null)
        }.create()
    }

    private fun createOriginDialog(): AlertDialog {
        val currentValue = preference.getValue(context) as LatLng
        var latitudeEdit: EditText? = null
        var longitudeEdit: EditText? = null
        val contentView: View = context.verticalLayout {
            lparams(width = matchParent, height = wrapContent)
            padding = dimen(R.dimen.element_spacing_x2)

            textInputLayout {
                hint = context.getString(R.string.pref_origin_latitude_hint)
                latitudeEdit = textInputEditText {
                     // Workaround for Anko issue (https://github.com/Kotlin/anko/issues/264)
                    layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setText(currentValue.latitude.toString())
                }
            }.lparams(width = matchParent, height = wrapContent)

            textInputLayout {
                hint = context.getString(R.string.pref_origin_longitude_hint)
                longitudeEdit = textInputEditText {
                    // Workaround for Anko issue (https://github.com/Kotlin/anko/issues/264)
                    layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setText(currentValue.longitude.toString())
                }
            }.lparams(width = matchParent, height = wrapContent) {
                topMargin = dimen(R.dimen.element_spacing_x1)
            }
        }
        return AlertDialog.Builder(context).apply {
            setTitle(R.string.pref_origin_title)
            setView(contentView)
            setPositiveButton(R.string.dialog_ok, { dialog, i ->

                latitudeEdit?.text?.toString()?.let { latitude ->
                    longitudeEdit?.text?.toString()?.let { longitude ->
                        val newLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
                        if (newLatLng == currentValue) {
                            return@setPositiveButton
                        }
                        PreferenceManager.updateOrigin(newLatLng, context)
                        RxBus.post(PreferenceUpdateEvent(preference.key))
                    }
                }
            })
            setNegativeButton(R.string.dialog_cancel, null)
        }.create()
    }
}