package com.example.worstmixer.worsts

import android.content.Context
import com.example.worstmixer.Worst
import com.example.worstmixer.XY

fun bananaworst(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime
    var newY = worst.position.y + worst.velocity.y * deltaTime

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()
    worst.resetPositionIfOffScreen()
    // Create a new Worst object with updated position and sprite
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        image = worst.getImageForType(context, worst.worstType, worst.size)  // Update the sprite based on worstType
    )

    // Return the new Worst object with updated properties
    return newWorst
}