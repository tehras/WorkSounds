package com.github.tehras.workmode.models.scene

import com.github.tehras.workmode.R

enum class TileImage(var tile: Int, var blackTile: Int, var position: Int) {
    HOME(R.drawable.ic_home_tile, R.drawable.ic_home_tile_black, 0),
    CAR(R.drawable.ic_car_tile, R.drawable.ic_car_tile_black, 1),
    WORK(R.drawable.ic_work_tile, R.drawable.ic_work_tile_black, 2),
    BLACK(R.drawable.ic_shopping, R.drawable.ic_shopping_black, 3),
    NONE(-1, -1, 4);

    companion object {
        fun getItem(position: Int): TileImage {
            values().forEach {
                if (position == it.position)
                    return it
            }

            return NONE
        }
    }
}
