package com.example.worstmixer.worsts

import android.content.Context
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import kotlin.concurrent.timer

fun weiswurst(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime * 5
    var newY = worst.position.y + worst.velocity.y * deltaTime * 5

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()

    // Random movement for Weiswurst
    val randomDirection = kotlin.random.Random.nextDouble(0.0, 360.0)
    val randomSpeed = kotlin.random.Random.nextDouble(5.0, 15.0)

    // Random chance to change direction
    if (kotlin.random.Random.nextInt(0, 20) < 3) {
        worst.velocity.x = randomSpeed * Math.cos(Math.toRadians(randomDirection))
        worst.velocity.y = randomSpeed * Math.sin(Math.toRadians(randomDirection))
    }

    // Visibility control
    val visibilityDuration = 3000L // Duration of visibility/invisibility in milliseconds
    val currentTime = System.currentTimeMillis()
    if (currentTime - worst.lastSparkleTime >= visibilityDuration) {
        worst.isVisible = !worst.isVisible
        worst.opacity = if (worst.isVisible) 1.0f else 0.0f
        worst.lastSparkleTime = currentTime
    }

    worst.resetPositionIfOffScreen()

    // Create a new Worst object with updated position, sprite, and opacity
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        image = worst.getImageForType(context, worst.worstType, worst.size),
        isVisible = worst.isVisible,
        opacity = worst.opacity
    )
    return newWorst
}

