package com.github.tehras.workmode.ui.preferencesetup.addnewgroup.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.github.tehras.workmode.R
import com.github.tehras.workmode.extensions.inflateLayoutFromParent
import com.github.tehras.workmode.models.scene.TileImage

class ImagePickerAdapter(var selectedTile: TileImage, var onTileChanged: (TileImage) -> Unit) : RecyclerView.Adapter<ImagePickerViewHolder>() {

    override fun onBindViewHolder(holder: ImagePickerViewHolder, position: Int) {
        holder.bindView(selectedTile, TileImage.getItem(position), { tile ->
            selectedTile = tile
            onTileChanged(tile)
            notifyDataSetChanged() // this will refresh the selected item
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePickerViewHolder {
        return ImagePickerViewHolder(parent.inflateLayoutFromParent(R.layout.list_item_image_picker))
    }

    override fun getItemCount(): Int {
        //this is actually static
        return TileImage.values().count() - 1
    }

}