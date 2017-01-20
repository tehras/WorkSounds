package com.github.tehras.workmode.views

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.github.tehras.workmode.R
import kotlinx.android.synthetic.main.view_success_layout.view.*

@Suppress("UNUSED")
class SuccessLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null)

    init {
        View.inflate(this.context, R.layout.view_success_layout, this)
    }

    fun show(postAnim: () -> Unit) {
        this.visibility = View.VISIBLE
        (success_checkmark.drawable as Animatable).start()

        Handler().postDelayed({ postAnim() }, 500)
    }

    fun hide() {
        this.visibility = View.GONE
    }

}