package com.github.tehras.workmode.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.ColorInt
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

fun Context.getColorDefault(@ColorInt color: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return this.resources.getColor(color, null)
    } else {
        @Suppress("DEPRECATION")
        return this.resources.getColor(color)
    }
}