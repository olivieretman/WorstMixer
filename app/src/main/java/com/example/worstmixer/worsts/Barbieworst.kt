package com.example.worstmixer.worsts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Size
import com.example.worstmixer.Worst
import com.example.worstmixer.XY

fun barbieworst(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime
    var newY = worst.position.y + worst.velocity.y * deltaTime

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()
    worst.resetPositionIfOffScreen()

    // Handle sparkle effect
    val newWorst = if (shouldSparkle(worst)) {
        worst.copy(
            initialPosition = XY(newX, newY),
            image = worst.getSparkleImage(context, worst.worstType, worst.size)  // Use sparkle effect image
        )
    } else {
        worst.copy(
            initialPosition = XY(newX, newY),
            image = worst.getImageForType(context, worst.worstType, worst.size)  // Normal image
        )
    }

    // Return the updated Worst object
    return newWorst
}

fun shouldSparkle(worst: Worst): Boolean {
    // Check if 5 seconds have passed to trigger sparkle
    val currentTime = System.currentTimeMillis()
    val lastSparkleTime = worst.lastSparkleTime

    if (currentTime - lastSparkleTime >= 1000L) {
        worst.lastSparkleTime = currentTime // Reset the sparkle time
        return true
    }
    return false
}

fun Worst.getSparkleImage(context: Context, worstType: Int, size: Size): Bitmap? {
    // Get the normal image for the Worst
    val normalImage = getImageForType(context, worstType, size) ?: return null

    // Create a copy of the original image to apply changes to
    val sparklingImage = normalImage.config?.let { normalImage.copy(it, true) }

    // Create a Canvas to apply the color filter
    val canvas = sparklingImage?.let { Canvas(it) }

    // Create a ColorMatrix to increase saturation and brightness
    val colorMatrix = ColorMatrix().apply {
        // Scale the saturation for a sparkling effect (e.g., 1.5 for more saturated)
        setSaturation(1.5f)

        // Increase brightness (e.g., 1.2 for a bit brighter)
        preConcat(ColorMatrix().apply {
            setScale(1.2f, 1.2f, 1.2f, 1f) // Increasing RGB channels
        })
    }

    // Apply the ColorMatrix as a ColorFilter
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    // Draw the modified image on the canvas
    if (canvas != null) {
        canvas.drawBitmap(normalImage, 0f, 0f, paint)
    }

    return sparklingImage
}