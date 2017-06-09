/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import jp.gaprot.omosanmaps.R
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

/**
 *  @See Preference
 */
class PreferenceAdapter(

        preferences: List<Preference> = emptyList(),

        private val onItemClick: (Preference) -> Unit

) : RecyclerView.Adapter<PreferenceHolder>() {

    var preferences: List<Preference> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        this.preferences = preferences
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PreferenceHolder {
        return PreferenceHolder(LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_preference, parent, false))
    }

    override fun onBindViewHolder(holder: PreferenceHolder?, position: Int) {
        val preference = preferences[position]
        holder?.preference = preference
        holder?.itemView?.onClick { onItemClick(preference) }
    }

    override fun getItemCount(): Int {
        return preferences.count()
    }

    fun update(key: String) {
        val old = preferences.find { it.key == key }
        val position = preferences.indexOf(old)
        notifyItemChanged(position)
    }
}

class PreferenceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val keyText: TextView

    val valueText: TextView

    var preference: Preference? = null
        set(pref) {
            if (pref == null) return
            itemView.context.let {
                keyText.text = pref.prettyKey(it)
                valueText.text = pref.prettyValue(it)
            }
        }

    init {
        keyText = itemView.find(R.id.preference_key)
        valueText = itemView.find(R.id.preference_value)
    }
}
