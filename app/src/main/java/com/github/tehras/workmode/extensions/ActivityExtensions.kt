package com.github.tehras.workmode.extensions

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.getLastFragmentInStack(): Fragment? {
    return this.supportFragmentManager.fragments.lastOrNull()
}