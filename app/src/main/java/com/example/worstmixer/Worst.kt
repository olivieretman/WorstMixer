// File: Worst.kt
package com.example.worstmixer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.Size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import com.example.worstmixer.data.EffectSpawner
import com.example.worstmixer.worsts.bananaworst
import com.example.worstmixer.worsts.barbieworst
import com.example.worstmixer.worsts.bloodworst
import com.example.worstmixer.worsts.braatworst
import com.example.worstmixer.worsts.cocktailworst
import com.example.worstmixer.worsts.darthvaderworst
import com.example.worstmixer.worsts.frikandel
import com.example.worstmixer.worsts.holyworst
import com.example.worstmixer.worsts.hongaarsworst
import com.example.worstmixer.worsts.hotdog
import com.example.worstmixer.worsts.koreanworst
import com.example.worstmixer.worsts.mummyworst
import com.example.worstmixer.worsts.rookworst
import com.example.worstmixer.worsts.sausijs
import com.example.worstmixer.worsts.suitworst
import com.example.worstmixer.worsts.superworst
import com.example.worstmixer.worsts.weiswurst
import kotlin.math.sqrt

data class XY(var x: Double, var y: Double)

data class Worst(
    val initialPosition: XY,
    var size: Size,
    var image: Bitmap? = null,
    var worstType: Int = 1,
    var income: Long = 0,
    var SCREEN_WIDTH: Int = 1080,
    var SCREEN_HEIGHT: Int = 1920,
    private val onMerged: (() -> Unit)? = null,
    private val holyworst: (() -> Unit)? = null,
    var isVisible: Boolean = true,
    var opacity: Float = 1.0f,
    var lastSparkleTime: Long = System.currentTimeMillis(),
    var velocity: XY = XY(3.0, 3.0),
    var onCoinsUpdated: ((Long) -> Unit)? = null
) {
    var position by mutableStateOf(initialPosition)
    var isDragging = false
        private set
    var collidingWith: Worst? = null
    var rotationSpeed = 0
    private var lastCoinGenerationTime = System.currentTimeMillis()
    fun getBoundingBox(): Rect {
        return Rect(
            position.x.toInt(),
            position.y.toInt(),
            (position.x + size.width).toInt(),
            (position.y + size.height).toInt()
        )
    }
    private fun generateCoins(): Long {
        return when (worstType) {
            1 -> 1  // Example: cocktailworst generates 10 coins
            2 -> 2  // Example: hotdog generates 15 coins
            3 -> 4   // Example: weiswurst generates 5 coins
            4 -> 16   // Example: weiswurst generates 5 coins
            5 -> 256   // Example: weiswurst generates 5 coins
            6 -> 5500   // Example: weiswurst generates 5 coins
            7 -> 65000   // Example: weiswurst generates 5 coins
            8 -> 365000   // Example: weiswurst generates 5 coins
            9 -> 4294967   // Example: weiswurst generates 5 coins
            10 -> 4294967 * 2   // Example: weiswurst generates 5 coins
            12 -> 4294967 * 4   // Example: weiswurst generates 5 coins
            13 -> 4294967 * 8   // Example: weiswurst generates 5 coins
            14 -> 4294967 * 16   // Example: weiswurst generates 5 coins
            15 -> 4294967 * 32   // Example: weiswurst generates 5 coins
            16 -> 4294967 * 64   // Example: weiswurst generates 5 coins
            17 -> 4294967 * 128   // Example: weiswurst generates 5 coins
            else -> 1 // Default for any other worst type
        }
    }
    fun applyOpacity(): android.graphics.Paint {
        val paint = android.graphics.Paint()
        paint.alpha = (opacity * 255).toInt()  // Set opacity from 0 to 255
        return paint
    }
    fun collidesWith(other: Worst): Boolean {
        val dx = this.position.x - other.position.x
        val dy = this.position.y - other.position.y
        val distance = sqrt(dx * dx + dy * dy)

        return distance < ((this.size.height / 2) + (other.size.height / 2))  // Adjust based on size
    }
    fun checkCollision(other: Worst): Boolean {
        return getBoundingBox().intersect(other.getBoundingBox())
    }
    fun reset(){
        size = Size(160, 160)
        opacity = 1.0f
    }
    fun update(context: Context, deltaTime: Double, allWorsts: List<Worst>) {
        val currentTime = System.currentTimeMillis()
        if (!isDragging) {
            when(worstType){
                1 -> {
                    val updatedWorst = cocktailworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                2 -> {
                    val updatedWorst = hotdog(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                3 -> {
                    val updatedWorst = weiswurst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                4 -> {
                    val updatedWorst = frikandel(context, deltaTime, frikandel = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                5 -> {
                    val updatedWorst = braatworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                6 -> {
                    val updatedWorst = hongaarsworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                7 -> {
                    val updatedWorst = bloodworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                8 -> {
                    val updatedWorst = sausijs(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                9 -> {
                    val updatedWorst = holyworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                    holyworst()
                }
                10 -> {
                    val updatedWorst = mummyworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                11 -> {
                    val updatedWorst = barbieworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                12 -> {
                    val updatedWorst = bananaworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                13 -> {
                    val updatedWorst = suitworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                14 -> {
                    val updatedWorst = darthvaderworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                15 -> {
                    val updatedWorst = rookworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                16 -> {
                    val updatedWorst = koreanworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
                17 -> {
                    val updatedWorst = superworst(context, deltaTime, worst = this)
                    position = updatedWorst.position  // Update the Worst position
                    image = updatedWorst.image  // Update the sprite (image)
                }
            }
        }
        val allWorstsCopy = allWorsts.toMutableList()
        val collidedWorst = allWorstsCopy.find { it !== this && collidesWith(it) }

        if (collidedWorst != null) {
            reset()
            // Use the copied list for merging
            mergeWith(collidedWorst, allWorstsCopy.toMutableList(), context)
        }

// After iteration, apply any list modifications
        if (currentTime - lastCoinGenerationTime >= 1000) {
            lastCoinGenerationTime = currentTime
            println("Generating coins...")
            onCoinsUpdated?.invoke(generateCoins())
        }

        resetPositionIfOffScreen()
    }
    fun mergeWith(other: Worst, allWorsts: MutableList<Worst>, context: Context) {
        if (this.worstType != other.worstType) return  // Only merge same types

        val newWorstType = this.worstType + 1
        val newX = (this.position.x + other.position.x) / 2
        val newY = (this.position.y + other.position.y) / 2

        val mergedWorst = Worst(
            initialPosition = XY(newX, newY),
            size = this.size,
            worstType = newWorstType,
            income = this.income * 2,
            SCREEN_WIDTH = this.SCREEN_WIDTH,
            SCREEN_HEIGHT = this.SCREEN_HEIGHT,
            onMerged = onMerged,
            holyworst = holyworst,
            velocity = this.velocity,
            onCoinsUpdated = onCoinsUpdated
        )
        mergedWorst.image = getImageForType(context, newWorstType, this.size)

        allWorsts.remove(this)
        allWorsts.remove(other)
        allWorsts.add(mergedWorst)

        playMergeSound(context)
        onMerged?.invoke()
    }


    fun resetPositionIfOffScreen() {
        if (position.x + size.width < 0.0 || position.x > SCREEN_WIDTH || position.y + size.height < 0.0 || position.y > SCREEN_HEIGHT) {
            position = XY(
                (0..maxOf(0, SCREEN_WIDTH - size.width.toInt())).random().toDouble(),
                (0..maxOf(0, SCREEN_HEIGHT - size.height.toInt())).random().toDouble()
            )
        }
    }
    fun holyworst(){
        holyworst?.invoke()
    }
    fun handleDrag(isDragStart: Boolean, x: Float, y: Float) {
        if (isDragStart) {
            if (getBoundingBox().contains(x.toInt(), y.toInt())) {
                isDragging = true
            }
        } else {
            if (isDragging) {
                position = XY((x - size.width / 2).toDouble(), (y - size.height / 2).toDouble())
            }
        }
    }

    fun stopDragging() {
        isDragging = false
        collidingWith = null
    }

    fun getImageForType(context: Context, type: Int, size: Size): Bitmap? {
            return when (type) {
                1 -> context.getDrawable(R.drawable.cocktailworst)?.toBitmap(size.width, size.height)
                2 -> context.getDrawable(R.drawable.hotdog)?.toBitmap(size.width, size.height)
                3 -> context.getDrawable(R.drawable.weiswurst)?.toBitmap(size.width, size.height)
                4 -> context.getDrawable(R.drawable.frikandel)?.toBitmap(size.width, size.height)
                5 -> context.getDrawable(R.drawable.braatworst)?.toBitmap(size.width, size.height)
                6 -> context.getDrawable(R.drawable.hongaarsworstje)?.toBitmap(size.width, size.height)
                7 -> context.getDrawable(R.drawable.bloodworst)?.toBitmap(size.width, size.height)
                8 -> context.getDrawable(R.drawable.sausijs)?.toBitmap(size.width, size.height)
                9 -> context.getDrawable(R.drawable.holyworst)?.toBitmap(size.width, size.height)
                10 -> context.getDrawable(R.drawable.mummyworst)?.toBitmap(size.width, size.height)
                11 -> context.getDrawable(R.drawable.barbieworst)?.toBitmap(size.width, size.height)
                12 -> context.getDrawable(R.drawable.bananaworst)?.toBitmap(size.width, size.height)
                13 -> context.getDrawable(R.drawable.suitworst)?.toBitmap(size.width, size.height)
                14 -> context.getDrawable(R.drawable.darthvaderworst)?.toBitmap(size.width, size.height)
                15 -> context.getDrawable(R.drawable.rookworst)?.toBitmap(size.width, size.height)
                16 -> context.getDrawable(R.drawable.koreanworst)?.toBitmap(size.width, size.height)
                17 -> context.getDrawable(R.drawable.supersausage)?.toBitmap(size.width, size.height)
                else -> context.getDrawable(R.drawable.cocktailworst)?.toBitmap(size.width, size.height)
            }
    }

    private fun playMergeSound(context: Context) {
        val mp = MediaPlayer.create(context, R.raw.merge)
        mp.setOnCompletionListener { it.release() }
        mp.start()
    }
}
