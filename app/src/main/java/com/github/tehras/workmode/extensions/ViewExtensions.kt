package com.github.tehras.workmode.extensions

import android.graphics.PorterDuff
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button

fun Button.setButtonColor(@IntegerRes color: Int) {
    @Suppress("DEPRECATION")
    this.background.setColorFilter(this.resources.getColor(color), PorterDuff.Mode.SRC_ATOP)
}

fun RecyclerView.defaultInit() {
    this.setHasFixedSize(true)
    this.layoutManager = LinearLayoutManager(this.context)
}