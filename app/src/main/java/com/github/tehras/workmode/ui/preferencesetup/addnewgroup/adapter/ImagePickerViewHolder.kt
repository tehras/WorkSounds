package com.github.tehras.workmode.ui.preferencesetup.addnewgroup.adapter

import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.github.tehras.workmode.R
import com.github.tehras.workmode.models.scene.TileImage

class ImagePickerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(selectedTileImage: TileImage?, tile: TileImage, imageSelected: (TileImage) -> Unit) {
        val selectableView = itemView.findViewById(R.id.tile_selectable)

        (itemView.findViewById(R.id.tile_image) as ImageView).setImageResource(tile.blackTile)
        selectableView.setOnClickListener { imageSelected(tile) }

        changeBackground(android.R.color.white, selectableView)
        selectedTileImage?.let {
            if (it == tile)
                changeBackground(R.color.colorAccent, selectableView)
        }

    }

    private fun changeBackground(@ColorInt background: Int, v: View) {
        @Suppress("DEPRECATION")
        v.setBackgroundColor(v.context.resources.getColor(background))
    }
}