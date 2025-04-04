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

fun sausijs(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime
    var newY = worst.position.y + worst.velocity.y * deltaTime

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()
    worst.resetPositionIfOffScreen()

    // Handle rainbow effect
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        image = worst.getRainbowImage(context, worst.worstType, worst.size)  // Apply rainbow color change
    )

    // Return the updated Worst object
    return newWorst
}

fun Worst.getRainbowImage(context: Context, worstType: Int, size: Size): Bitmap? {
    // Get the normal image for the Worst
    val normalImage = getImageForType(context, worstType, size) ?: return null

    // Create a copy of the original image to apply changes to
    val rainbowImage = normalImage.config?.let { normalImage.copy(it, true) }

    // Get the current time (this will drive the color change over time)
    val time = System.currentTimeMillis() % 1000L

    // Calculate hue shift based on time (this gives a smooth rainbow cycle)
    val hueShift = time.toFloat() / 1000f * 360f  // Full 360-degree hue cycle

    // Create a Canvas to apply the color filter
    val canvas = rainbowImage?.let { Canvas(it) }

    // Create a ColorMatrix to apply the hue shift (rainbow effect)
    val colorMatrix = ColorMatrix().apply {
        // Apply hue rotation (this will shift the colors over time)
        val hue = (hueShift - 180)  // Shift the hue within the 360-degree color wheel
        setRotate(0, hue) // Apply hue shift on Red channel
        setRotate(1, hue) // Apply hue shift on Green channel
        setRotate(2, hue) // Apply hue shift on Blue channel
    }

    // Apply the ColorMatrix as a ColorFilter
    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    // Draw the modified image on the canvas
    if (canvas != null) {
        canvas.drawBitmap(normalImage, 0f, 0f, paint)
    }

    return rainbowImage
}
