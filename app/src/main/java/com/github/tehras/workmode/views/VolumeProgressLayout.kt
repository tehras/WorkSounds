package com.github.tehras.workmode.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.github.tehras.workmode.R
import kotlinx.android.synthetic.main.view_volume_progress_bar.view.*


@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
class VolumeProgressLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?) : this(context, null)


    private val RING = 0
    private val MEDIA = 1
    private var volType = -1

    init {
        val ta = context?.obtainStyledAttributes(attrs, R.styleable.VolumeProgressLayout, 0, 0)
        var volEnabled = false

        try {
            volEnabled = ta?.getBoolean(R.styleable.VolumeProgressLayout_volume_enabled, false) ?: false
            volType = ta?.getInteger(R.styleable.VolumeProgressLayout_volume_image, -1) ?: -1
        } finally {
            ta?.recycle()
        }

        inflate(context, R.layout.view_volume_progress_bar, this)

        when (volType) {
            -1 -> view_volume_image.visibility = View.INVISIBLE
            RING -> view_volume_image.setImageResource(R.drawable.ic_sound_enabled)
            MEDIA -> view_volume_image.setImageResource(R.drawable.ic_bell_enabled)
        }

        view_volume_seek_bar.isEnabled = volEnabled
    }

    /**
     * @param vol -> Current Volume Level
     * @param maxVol -> Max Volume Levels
     */
    @Suppress("unused")
    fun setVolumeLevel(vol: Int, maxVol: Int) {
        updateIcon(vol == 0)
        view_volume_seek_bar.progress = vol
        view_volume_seek_bar.max = maxVol
    }

    /**
     * @param b -> True means it's 0
     */
    private fun updateIcon(b: Boolean) {
        when (volType) {
            RING -> if (b) view_volume_image.setImageResource(R.drawable.ic_sound_disabled) else view_volume_image.setImageResource(R.drawable.ic_sound_enabled)
            MEDIA -> if (b) view_volume_image.setImageResource(R.drawable.ic_bell_disabled) else view_volume_image.setImageResource(R.drawable.ic_bell_enabled)
        }
    }
}