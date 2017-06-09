/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.folder

import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import jp.gaprot.omosanmaps.ListUiContract
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.extension.actionBarSize
import jp.gaprot.omosanmaps.extension.color
import jp.gaprot.omosanmaps.extension.selectableItemBackground
import jp.gaprot.omosanmaps.widget.recycler.DividerItemDecoration
import jp.gaprot.omosanmaps.widget.recycler.EntityAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

class FolderListActivityUI(

        private val folderAdapter: EntityAdapter<Folder>,

        private val onRetryButtonClick: () -> Unit

) : AnkoComponent<FolderListActivity>, ListUiContract() {

    override lateinit var listContainer: View

    override lateinit var loadingView: View

    override lateinit var emptyView: View

    override fun createView(ui: AnkoContext<FolderListActivity>) = ui.apply {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)
            fitsSystemWindows = true

            appBarLayout(theme = R.style.AppTheme_AppBarOverlay) {
                toolbar {
                    setTitle(R.string.app_name)
                    setTitleTextColor(context.color(R.color.white))
                    backgroundColor = context.color(R.color.colorPrimary)
                    popupTheme = R.style.AppTheme_PopupOverlay
                }.lparams {
                    width = matchParent
                    height = context.actionBarSize
                }.let {
                    owner.setSupportActionBar(it)
                }

            }.lparams(width = matchParent, height = wrapContent)

            frameLayout {

                listContainer = recyclerView {
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(DividerItemDecoration(context))
                    adapter = folderAdapter
                }.lparams(width = matchParent, height = matchParent)

                loadingView = progressBar().lparams(gravity = Gravity.CENTER)

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
                        onClick { onRetryButtonClick.invoke() }
                    }.lparams {
                        width = wrapContent
                        height = wrapContent
                        below(emptyText)
                        centerHorizontally()
                    }
                }

            }.lparams {
                width = matchParent
                height = matchParent
                topMargin = context.actionBarSize
            }
        }
    }.view

    fun setFolders(folders: List<Folder>) {
        folderAdapter.setData(folders)
    }
}