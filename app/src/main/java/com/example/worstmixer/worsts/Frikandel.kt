package com.example.worstmixer.worsts

import android.content.Context
import android.util.Size
import androidx.core.graphics.drawable.toBitmap
import com.example.worstmixer.R
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import com.example.worstmixer.data.EffectSpawner

fun frikandel(context: Context, deltaTime: Double, frikandel: Worst): Worst {
    // Update Frikandel positie
    var newX = frikandel.position.x + frikandel.velocity.x * deltaTime
    var newY = frikandel.position.y + frikandel.velocity.y * deltaTime

    // Wrap around screen logic
    if (newX + frikandel.size.width < 0) newX = frikandel.SCREEN_WIDTH.toDouble()
    if (newX > frikandel.SCREEN_WIDTH) newX = -frikandel.size.width.toDouble()
    if (newY + frikandel.size.height < 0) newY = frikandel.SCREEN_HEIGHT.toDouble()
    if (newY > frikandel.SCREEN_HEIGHT) newY = -frikandel.size.height.toDouble()


    frikandel.resetPositionIfOffScreen()
    // Update de Frikandel en geef de spawner door
    return frikandel.copy(
        initialPosition = XY(newX, newY),
        image = frikandel.getImageForType(context, 4, frikandel.size),
    )
}
