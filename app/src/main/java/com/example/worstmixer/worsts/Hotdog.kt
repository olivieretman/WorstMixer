package com.example.worstmixer.worsts

import android.content.Context
import android.util.Size
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import kotlin.random.Random

fun hotdog(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime * 5
    var newY = worst.position.y + worst.velocity.y * deltaTime * 5

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()

    // Random movement for Hotdog, similar to the Java logic
    val randomDirection = Random.nextDouble(0.0, 360.0)
    val randomSpeed = Random.nextDouble(5.0, 15.0)

    // Random chance to change direction
    if (Random.nextInt(0, 20) < 3) {
        worst.velocity.x = randomSpeed * Math.cos(Math.toRadians(randomDirection))
        worst.velocity.y = randomSpeed * Math.sin(Math.toRadians(randomDirection))
    }

    // Random rotation speed
    val randomRotationSpeed = Random.nextInt(0, 20)
    worst.rotationSpeed = randomRotationSpeed

    // Randomly shrink or grow the Worst object (with a certain probability)
    if (Random.nextInt(0, 100) < 2) {  // 2% chance to shrink or grow
        val scaleFactor = Random.nextDouble(0.8, 1.2)  // Random scaling factor between 0.8 (shrink) and 1.2 (grow)
        if ((worst.size.height >= 160 && scaleFactor < 1.0) || (worst.size.height <= 160 && scaleFactor > 1.0)) {
            worst.size = Size(
                (worst.size.width * scaleFactor).toInt(),
                (worst.size.height * scaleFactor).toInt()
            )
        }
    }
    worst.resetPositionIfOffScreen()
    // Create a new Worst object with updated position, sprite, and size
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        image = worst.getImageForType(context, worst.worstType, worst.size)
    )

    // Return the updated Worst object with the new position, velocity, rotation speed, and size
    return newWorst
}
