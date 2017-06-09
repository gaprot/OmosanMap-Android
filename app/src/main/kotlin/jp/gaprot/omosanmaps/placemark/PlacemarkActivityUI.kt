/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.placemark

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.extension.color
import org.jetbrains.anko.*
import org.jetbrains.anko.design._CoordinatorLayout
import org.jetbrains.anko.design.coordinatorLayout

class PlacemarkActivityUI(

        private val onMapReadyCallback: OnMapReadyCallback,

        private val onSort: () -> Unit,

        private val onFilter: () -> Unit

) : AnkoComponent<PlacemarkActivity> {

    val mapFragment: SupportMapFragment

    lateinit var bottomSheet: View
        private set

    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
        private set

    lateinit var mapView: View
        private set

    lateinit var bottomActionBar: View
        private set

    init {
        mapFragment = SupportMapFragment.newInstance()
    }

    override fun createView(ui: AnkoContext<PlacemarkActivity>) = ui.apply {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)
            fitsSystemWindows = true

            mapView = frameLayout {
                id = R.id.map_fragment_container
            }.lparams {
                width = matchParent
                height = matchParent
            }
            setupMapFragment(owner)

            bottomSheetBehavior = createBottomSheetBehavior(this)

            verticalLayout {
                id = R.id.bottom_sheet_layout
                fitsSystemWindows = true

                bottomSheet = frameLayout {
                    id = R.id.bottom_sheet_container
                    backgroundColor = context.color(android.R.color.white)
                }.lparams(width = matchParent, height = matchParent)

            }.lparams {
                width = matchParent
                height = matchParent
                behavior = bottomSheetBehavior
            }

            bottomActionBar = relativeLayout {

                // タッチイベント対策(これがないと、後ろのRecyclerViewにタッチイベントが伝搬してしまう)
                onClick {
                    Log.d("PlacemarkActivityUI", "createView: onClick bottom action bar")
                }

                val shadow = view {
                    id = R.id.bottom_actionbar_shadow
                    backgroundResource = R.drawable.bg_shadow_inverse
                }.lparams {
                    width = matchParent
                    height = dimen(R.dimen.shadow_height)
                    gravity = Gravity.TOP
                }

                relativeLayout {
                    padding = dimen(R.dimen.element_spacing_x2)

                    backgroundColor = context.color(R.color.white)
                    textView {
                        setText(R.string.action_sort)
                        textSizeDimen = R.dimen.text_size_subhead
                        textColor = context.color(R.color.text_black)
                        onClick { showSortDialog(context) }
                    }.lparams {
                        alignParentStart()
                    }
                    textView {
                        setText(R.string.action_filter)
                        textSizeDimen = R.dimen.text_size_subhead
                        textColor = context.color(R.color.text_black)
                        onClick { onFilter.invoke() }
                    }.lparams {
                        alignParentEnd()
                    }
                }.lparams {
                    width = matchParent
                    height = wrapContent
                    gravity = Gravity.BOTTOM
                    below(shadow)
                }
            }.lparams {
                width = matchParent
                height = wrapContent
                gravity = Gravity.BOTTOM
            }
        }

    }.view

    private fun setupMapFragment(activity: FragmentActivity) {
        activity.supportFragmentManager.beginTransaction()
                .add(R.id.map_fragment_container, mapFragment)
                .commit()
        mapFragment.getMapAsync(onMapReadyCallback)
    }

    private fun createBottomSheetBehavior(parent: _CoordinatorLayout): BottomSheetBehavior<LinearLayout> {
        val context = parent.context
        return BottomSheetBehavior<LinearLayout>(context, null).apply {
            peekHeight = context.dimen(R.dimen.peek_height)
            isHideable = false
        }
    }

    private fun showSortDialog(context: Context) {
        AlertDialog.Builder(context)
                .setMessage(R.string.sort_dialog_message)
                .setPositiveButton(R.string.sort_dialog_positive, { dialog, button ->
                    onSort.invoke()
                })
                .setNegativeButton(R.string.sort_dialog_negative, null)
                .create().show()
    }
}