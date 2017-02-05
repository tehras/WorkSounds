package com.github.tehras.workmode.ui.base

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class AbstractViewHolder<in T>(view: View?) : RecyclerView.ViewHolder(view) {

    abstract fun bindView(t: T?, position: Int)

}