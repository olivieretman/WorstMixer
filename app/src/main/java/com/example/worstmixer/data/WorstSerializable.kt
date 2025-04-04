package com.example.worstmixer.data

import android.util.Size
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import kotlinx.serialization.Serializable


@Serializable
data class WorstSerializable(
    val x: Double,
    val y: Double,
    val sizeWidth: Int,
    val sizeHeight: Int,
    val worstType: Int,
    val income: Long,
    val SCREEN_WIDTH: Int = 1080,
    val SCREEN_HEIGHT: Int = 1920
) {
    fun toWorst(): Worst {
        // Use android.util.Size for the size property.
        return Worst(
            initialPosition = XY(x, y),
            size = Size(sizeWidth, sizeHeight),
            worstType = worstType,
            income = income,
            SCREEN_WIDTH = SCREEN_WIDTH,
            SCREEN_HEIGHT = SCREEN_HEIGHT
            // Note: Bitmap/image is not persisted. You’ll have to reload it as needed.
        )
    }
}

fun Worst.toSerializable(): WorstSerializable {
    return WorstSerializable(
        x = this.position.x,
        y = this.position.y,
        sizeWidth = this.size.width,
        sizeHeight = this.size.height,
        worstType = this.worstType,
        income = this.income,
        SCREEN_HEIGHT = this.SCREEN_HEIGHT,
        SCREEN_WIDTH = this.SCREEN_WIDTH
    )
}

