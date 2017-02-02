package com.github.tehras.workmode.extensions

import android.view.View
import android.view.ViewAnimationUtils

fun View.circularReveal() {
    val cx = this.width / 2
    val cy = this.height / 2
    val radius = 0
    val finalRadius = Math.max(this.width, this.height).toFloat()
    val circularReveal = ViewAnimationUtils.createCircularReveal(this, cx, cy, radius.toFloat(), finalRadius)
    circularReveal.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()

    circularReveal.start()
}

fun View.animateFromRight() {
    val width = this.width
    this.translationX = width.toFloat()

    this.animate().translationXBy(-width.toFloat()).start()
}

fun View.animateFromBottom() {
    val height = this.height
    this.translationY = height.toFloat()

    this.animate().translationYBy(-height.toFloat()).start()
}
