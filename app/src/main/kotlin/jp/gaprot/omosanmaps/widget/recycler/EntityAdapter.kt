/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.widget.recycler

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.model.abst.Entity
import org.jetbrains.anko.find

/**
 *  Entity のための RecyclerView.Adapter
 *  name を表示するためだけのシンプルなリストを作成
 *  @See Entity
 */
class EntityAdapter<E : Entity> @JvmOverloads constructor(

        private var data: List<E>,

        @LayoutRes
        private val layoutResId: Int = R.layout.item_single_line,

        @IdRes
        private val textViewId: Int = R.id.text_view

) : RecyclerView.Adapter<SingleLineViewHolder>() {

    private var listener: ((E) -> Unit)? = null

    fun setOnItemClickListener(listener: (E) -> Unit) {
        this.listener = listener
    }

    fun setData(data: List<E>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SingleLineViewHolder {
        return SingleLineViewHolder(LayoutInflater.from(parent?.context).inflate(layoutResId, parent, false), textViewId)
    }

    override fun onBindViewHolder(holder: SingleLineViewHolder?, position: Int) {
        val entity = data[position]
        holder?.textView?.text = entity.name
        holder?.itemView?.setOnClickListener {
            listener?.invoke(entity)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

class SingleLineViewHolder(

        itemView: View,

        @IdRes
        textViewId: Int

) : RecyclerView.ViewHolder(itemView) {

    var textView: TextView

    init {
        textView = itemView.find<TextView>(textViewId)
    }
}
