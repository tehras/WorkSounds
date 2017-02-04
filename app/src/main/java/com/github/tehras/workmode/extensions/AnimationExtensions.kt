package com.github.tehras.workmode.extensions

import android.animation.Animator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateInterpolator

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

    this.animate().translationXBy(-width.toFloat()).setVisible(this).setInterpolator(AccelerateInterpolator()).start()
}

fun View.animateFromLeft() {
    val width = this.width
    this.translationX = -width.toFloat()

    this.animate().translationXBy(width.toFloat()).setVisible(this).setInterpolator(AccelerateInterpolator()).start()
}

fun View.animateFromBottom() {
    val height = this.height
    this.translationY = height.toFloat()

    this.animate().translationYBy(-height.toFloat()).setVisible(this).setInterpolator(AccelerateInterpolator()).start()
}

fun View.animateOutToLeft() {
    val width = this.width
    this.translationX = 0.toFloat()

    this.animate().translationXBy(-width.toFloat()).setGone(this).start()
}

fun View.animateOutToRight() {
    val width = this.width
    this.translationX = 0.toFloat()

    this.animate().translationXBy(width.toFloat()).setGone(this).start()
}

private fun ViewPropertyAnimator.setVisible(view: View): ViewPropertyAnimator {
    return this.setListener(EzAnimatorListener({ view.visibility = View.VISIBLE }, {}))
}

private fun ViewPropertyAnimator.setGone(view: View): ViewPropertyAnimator {
    return this.setListener(EzAnimatorListener({}, {
        view.visibility = View.GONE
        view.translationX = 0.toFloat()
        view.translationY = 0.toFloat()
    }))
}

private class EzAnimatorListener(val startAnimation: () -> Unit, val endAnimation: () -> Unit) : Animator.AnimatorListener {

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        endAnimation()
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
        startAnimation()
    }

}
