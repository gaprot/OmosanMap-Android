/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.support.v7.widget.LinearLayoutManager
import android.widget.ScrollView
import jp.gaprot.omosanmaps.widget.recycler.DividerItemDecoration
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView

class PreferenceActivityUI(

        preferences: List<Preference> = emptyList(),

        onItemClick: (Preference) -> Unit

) : AnkoComponent<PreferenceActivity> {

    private val preferenceAdapter: PreferenceAdapter

    init {
        preferenceAdapter = PreferenceAdapter(preferences, onItemClick)
    }

    override fun createView(ui: AnkoContext<PreferenceActivity>) = ui.apply {

        frameLayout {

            lparams(width = matchParent, height = matchParent)

            recyclerView {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context))
                adapter = preferenceAdapter
                overScrollMode = ScrollView.OVER_SCROLL_NEVER
            }.lparams(width = matchParent, height = matchParent)
        }

    }.view

    fun update(key: String) {
        preferenceAdapter.update(key)
    }
}