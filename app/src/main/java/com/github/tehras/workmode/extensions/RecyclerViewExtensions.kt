package com.github.tehras.workmode.extensions

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflateLayoutFromParent(@LayoutRes layout: Int): View = LayoutInflater.from(this.context).inflate(layout, this, false)