package com.github.tehras.workmode.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

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