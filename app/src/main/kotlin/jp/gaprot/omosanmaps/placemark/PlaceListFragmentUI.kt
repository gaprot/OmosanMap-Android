/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.placemark

import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import jp.gaprot.omosanmaps.ListUiContract
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.extension.color
import jp.gaprot.omosanmaps.extension.selectableItemBackground
import jp.gaprot.omosanmaps.widget.recycler.DividerItemDecoration
import jp.gaprot.omosanmaps.widget.recycler.EntityAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class PlaceListFragmentUI(

        private val placemarkAdapter: EntityAdapter<Placemark>,

        private val onRetryButtonClick: (Unit) -> Unit

) : AnkoComponent<PlaceListFragment>, ListUiContract() {

    override lateinit var listContainer: View

    override lateinit var loadingView: View

    override lateinit var emptyView: View

    override fun createView(ui: AnkoContext<PlaceListFragment>) = ui.apply {
        frameLayout {
            lparams {
                width = matchParent
                height = matchParent
                minimumHeight = context.dimen(R.dimen.peek_height)
            }

            // Placemark list
            listContainer = recyclerView {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context))
                adapter = placemarkAdapter
            }.lparams {
                width = matchParent
                height = wrapContent
                bottomPadding = dip(48)
            }

            loadingView = progressBar().lparams {
                topMargin = dimen(R.dimen.element_spacing_x1)
                gravity = Gravity.CENTER_HORIZONTAL
            }

            // TextView and Button to retry
            emptyView = relativeLayout {
                lparams(width = matchParent, height = matchParent)
                val emptyText = textView {
                    id = R.id.folder_list_empty_text
                    text = context.getString(R.string.folder_list_no_folder)
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    centerInParent()
                    bottomMargin = dimen(R.dimen.element_spacing_x1)
                }

                button {
                    text = context.getString(R.string.folder_list_try_again)
                    backgroundDrawable = context.selectableItemBackground
                    textColor = context.color(R.color.text_black_sub)
                    onClick { onRetryButtonClick }
                }.lparams {
                    width = wrapContent
                    height = wrapContent
                    below(emptyText)
                    centerHorizontally()
                }
            }
        }
    }.view
}