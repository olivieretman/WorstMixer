package com.example.worstmixer.worsts

import android.content.Context
import android.util.Size
import com.example.worstmixer.Worst
import com.example.worstmixer.XY

fun mummyworst(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime
    var newY = worst.position.y + worst.velocity.y * deltaTime

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()
    worst.resetPositionIfOffScreen()

    // Apply "breathing" effect by changing size slightly over time
    val breathingFactor = 1 + Math.sin(System.currentTimeMillis() / 500.0) * 0.1 // Slight size fluctuation
    val newSize = Size((worst.size.width * breathingFactor).toInt(), (worst.size.height * breathingFactor).toInt())

    // Wander pattern by applying slight random variations to the velocity
    val randomWalkX = (Math.random() - 0.5) * 0.5 // Random change in X direction
    val randomWalkY = (Math.random() - 0.5) * 0.5 // Random change in Y direction
    val newVelocity = XY(worst.velocity.x + randomWalkX, worst.velocity.y + randomWalkY)

    // Create a new Worst object with updated position, size, and wandering velocity
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        size = newSize,
        velocity = newVelocity,
        image = worst.getImageForType(context, worst.worstType, newSize)  // Update the sprite based on worstType
    )

    // Return the new Worst object with updated properties
    return newWorst
}
