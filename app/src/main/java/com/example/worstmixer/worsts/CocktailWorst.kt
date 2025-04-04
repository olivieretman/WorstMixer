package com.example.worstmixer.worsts

import android.content.Context
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import kotlin.random.Random


fun cocktailworst(context: Context, deltaTime: Double, worst: Worst): Worst {
    // Increase the intensity of movement by applying larger steps in both X and Y directions.
    var newX = worst.position.x + worst.velocity.x * deltaTime * 5 // Multiply deltaTime for larger steps
    var newY = worst.position.y + worst.velocity.y * deltaTime * 5 // Multiply deltaTime for larger steps

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()

    // Add random movement periodically (simulating "jumping")
    val randomDirection = Random.nextDouble(0.0, 360.0)

    // Increase the random speed range for more intense movement (larger steps).
    val randomSpeed = Random.nextDouble(15.0, 30.0) // Increased range for larger movement

    // Apply new random velocity more frequently (increased probability of change)
    if (Random.nextInt(0, 10) < 3) {  // Increased chance to change direction (30%)
        worst.velocity.x = randomSpeed * Math.cos(Math.toRadians(randomDirection))
        worst.velocity.y = randomSpeed * Math.sin(Math.toRadians(randomDirection))
    }
    worst.resetPositionIfOffScreen()
    // Create a new Worst object with updated position and sprite
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        image = worst.getImageForType(context, worst.worstType, worst.size)  // Update the sprite based on worstType
    )

    // Return the updated Worst object with the new position and randomized velocity
    return newWorst
}
