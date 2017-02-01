package com.github.tehras.workmode.extensions

import android.graphics.Rect
import android.support.annotation.IntegerRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


fun ViewGroup.inflateLayoutFromParent(@LayoutRes layout: Int): View = LayoutInflater.from(this.context).inflate(layout, this, false)

fun RecyclerView.addBottomPadding(@IntegerRes size: Int) {
    this.addItemDecoration(BottomOffsetDecoration(this.context.resources.getDimensionPixelOffset(size)))
}

internal class BottomOffsetDecoration(private val bottomOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val dataSize = state.itemCount
        val position = parent.getChildAdapterPosition(view)
        if (dataSize > 0 && position == dataSize - 1) {
            outRect.set(0, 0, 0, bottomOffset)
        } else {
            outRect.set(0, 0, 0, 0)
        }

    }
}