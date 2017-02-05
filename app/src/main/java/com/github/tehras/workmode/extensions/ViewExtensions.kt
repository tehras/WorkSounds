package com.github.tehras.workmode.extensions

import android.content.Context
import android.content.res.Configuration
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntegerRes
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import timber.log.Timber

fun Button.setButtonColor(@IntegerRes color: Int) {
    @Suppress("DEPRECATION")
    this.background.setColorFilter(this.resources.getColor(color), PorterDuff.Mode.SRC_ATOP)
}

fun ImageButton.setButtonColor(@IntegerRes color: Int) {
    @Suppress("DEPRECATION")
    this.background.setColorFilter(this.resources.getColor(color), PorterDuff.Mode.SRC_ATOP)
}

fun RecyclerView.defaultInit() {
    this.setHasFixedSize(true)
    if (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
        this.layoutManager = LinearLayoutManager(this.context)
    else {
        this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

fun Snackbar.setTextColor(@ColorInt color: Int): Snackbar {
    val view = this.view
    val tv = view.findViewById(android.support.design.R.id.snackbar_text) as TextView
    tv.setTextColor(color)

    return this
}

fun Context.getColorDefault(@ColorInt color: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return this.resources.getColor(color, null)
    } else {
        @Suppress("DEPRECATION")
        return this.resources.getColor(color)
    }
}

fun EditText.addSimpleTextChangeListener(func: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            func(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

fun View.circularReveal(cx: Int, cy: Int, radius: Int) {
    this.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            this@circularReveal.removeOnLayoutChangeListener(this)

            val finalRadius = Math.max(this@circularReveal.width, this@circularReveal.height).toFloat()

            Timber.d("circular reveal -> centerX - $cx, centerY - $cy, r - $radius, R - $finalRadius")

            // create the animator for this view (the start radius is zero)
            val circularReveal = ViewAnimationUtils.createCircularReveal(this@circularReveal, cx, cy, radius.toFloat(), finalRadius)
            circularReveal.duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()

            Timber.d("circular reveal -> ${circularReveal.duration}")

            // make the view visible and start the animation
            this@circularReveal.visibility = View.VISIBLE
            circularReveal.start()
        }

    })
}