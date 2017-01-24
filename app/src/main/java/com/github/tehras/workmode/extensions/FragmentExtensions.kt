package com.github.tehras.workmode.extensions

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.github.tehras.workmode.R
import com.github.tehras.workmode.ui.base.BaseActivity

fun <T : Fragment> T.addToBundle(func: Bundle.() -> Unit): T {
    val args: Bundle = Bundle()

    args.func()
    this.arguments = arguments

    return this
}

@SuppressLint("PrivateResource", "CommitTransaction")
fun <T : Fragment> T.startFragment(activity: BaseActivity, view: View, animate: Boolean) {
    val tran = activity.supportFragmentManager.beginTransaction()

    if (animate)
        tran.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_shrink_fade_out_from_bottom)

    tran.replace(view.id, this, this.javaClass.simpleName)
            .addToBackStack(this.javaClass.simpleName)
            .commit()
}