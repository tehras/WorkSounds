package com.github.tehras.workmode.views

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.github.tehras.workmode.R


class SuccessLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null)

    init {
        View.inflate(this.context, R.layout.view_success_layout, this)

        val image = findViewById(R.id.success_checkmark) as ImageView
        (image.drawable as Animatable).start()
    }

}