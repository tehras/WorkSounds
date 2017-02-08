package com.github.tehras.workmode.ui.preferencesetup.addnewgroup

import android.view.View
import android.view.ViewTreeObserver
import android.widget.ScrollView
import com.github.tehras.workmode.views.DividerLayout

class VolumeNewListener(val scrollView: ScrollView, val topDivider: DividerLayout, vararg val divider: DividerLayout?) : ViewTreeObserver.OnScrollChangedListener, View.OnScrollChangeListener {

    var oldScrollY: Int = 0
    var oldScrollX: Int = 0

    override fun onScrollChanged() {
        onScrollChange(scrollView, scrollView.scrollX, scrollView.scrollY, oldScrollX, oldScrollY)
        oldScrollX = scrollView.scrollX
        oldScrollY = scrollView.scrollY
    }

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