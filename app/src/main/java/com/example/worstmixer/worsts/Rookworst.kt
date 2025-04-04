package com.example.worstmixer.worsts

import android.content.Context
import android.graphics.Bitmap
import com.example.worstmixer.Worst
import com.example.worstmixer.XY

fun rookworst(context: Context, deltaTime: Double, worst: Worst): Worst {
    var newX = worst.position.x + worst.velocity.x * deltaTime
    var newY = worst.position.y + worst.velocity.y * deltaTime

    // Wrap Around Screen Logic
    if (newX + worst.size.width < 0) newX = worst.SCREEN_WIDTH.toDouble()
    if (newX > worst.SCREEN_WIDTH) newX = -worst.size.width.toDouble()
    if (newY + worst.size.height < 0) newY = worst.SCREEN_HEIGHT.toDouble()
    if (newY > worst.SCREEN_HEIGHT) newY = -worst.size.height.toDouble()
    worst.resetPositionIfOffScreen()

    // Rotation effect: Increment the angle slightly over time to simulate a rotation
    val rotationAngle = (System.currentTimeMillis() / 50 % 360).toFloat()  // Rotate every 50ms

    // Zig-Zag Movement: Add a slight variation in movement along X and Y axes
    val zigZagFactor = Math.sin(System.currentTimeMillis() / 300.0) * 10  // A small zig-zag in movement
    val newVelocity = XY(worst.velocity.x + zigZagFactor, worst.velocity.y)

    // Update the Worst object with new position, rotation angle, and zig-zag movement
    val newWorst = worst.copy(
        initialPosition = XY(newX, newY),
        velocity = newVelocity,
        image = worst.getImageForType(context, worst.worstType, worst.size)  // Update sprite based on worstType
    )

    // Apply rotation effect on the image
    val rotatedImage = rotateImage(newWorst.image, rotationAngle)

    // Return the new Worst object with updated properties
    return newWorst.copy(image = rotatedImage)
}

fun rotateImage(original: Bitmap?, angle: Float): Bitmap? {
    if (original == null) return null

    val matrix = android.graphics.Matrix()
    matrix.postRotate(angle)

    // Create a new bitmap with the rotated image
    return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
}
