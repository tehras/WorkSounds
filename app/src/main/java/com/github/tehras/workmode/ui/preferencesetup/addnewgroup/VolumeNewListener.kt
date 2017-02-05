package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.view.View
import com.github.tehras.workmode.views.DividerLayout

class VolumeNewListener(val topDivider: DividerLayout, vararg val divider: DividerLayout?) : View.OnScrollChangeListener {

    init {
        topDivider.setText(divider.first()?.getText() ?: "")
    }

    override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        var div: DividerLayout? = null
        var almostDiv: DividerLayout? = null
        divider.forEach { d ->
            val top = d?.top ?: 0
            if (scrollY >= top)
                div = d
            else if (scrollY >= (top.minus(d?.height ?: 0))) {
                almostDiv = d
            }
        }

        div?.let {
            topDivider.setText(it.getText())
        }

        if (almostDiv != null) {
            val h = almostDiv!!.height
            val height = -h.plus(scrollY.minus(almostDiv!!.top))
            //start moving top divider
            topDivider.translationY = height.toFloat()
        } else {
            topDivider.translationY = 0.toFloat()
        }
    }

}