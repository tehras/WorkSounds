package com.github.tehras.workmode.views

import android.content.Context
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.tehras.workmode.R

@Suppress("unused")
class DividerLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, R.style.ElevatedLayout_NoEndMargin) {

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.style.ElevatedLayout_NoEndMargin)
    constructor(context: Context?) : this(context, null)

    private var text: TextView

    init {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.DividerLayout, 0, 0)

        val s: String

        try {
            s = ta?.getString(R.styleable.DividerLayout_divider_text) ?: ""
        } finally {
            ta?.recycle()
        }

        View.inflate(this.context, R.layout.view_divier_layout, this)

        this.text = findViewById(R.id.divider_text) as TextView

        setBackgroundResource(R.color.colorPrimaryDark)

        this.text.text = s
    }

    fun setTextColor(@ColorInt color: Int) {
        this.text.setTextColor(this.context.resources.getColor(color))
    }

    @Suppress("unused")
    fun setText(s: CharSequence) {
        this.text.text = s
    }

}