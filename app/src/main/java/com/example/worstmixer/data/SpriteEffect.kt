package com.example.worstmixer.data

import android.util.Size
import com.example.worstmixer.XY

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.core.graphics.drawable.toBitmap
import com.example.worstmixer.R
import kotlinx.coroutines.*


class SpriteEffect(
    val position: XY,
    val imagePath: Bitmap,
    val size: Size
) {
    var creationTime: Long = System.nanoTime()  // Track when the effect was created
    var isAlive = true
    var image: Bitmap = imagePath

    // This function would ideally spawn the effect (e.g., draw the onion) in the game

    // Update function for managing decay, check if the effect should still be alive
    fun updateDecay() {
        // If the effect has been alive for too long, mark it as dead
        val decayTimeLimit = 5_000_000_000L  // 5 seconds for example, adjust as needed
        if (System.nanoTime() - creationTime > decayTimeLimit) {
            isAlive = false
        }
    }
}

class EffectSpawner(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val effectSize: Int = 50,
    private val bitmap: Bitmap
) {
    val effects = mutableStateListOf<SpriteEffect>()
    private var spawnerJob: Job? = null

    fun startSpawning() {
        spawnerJob?.cancel()
        spawnerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val randomX = (0..screenWidth - effectSize).random().toDouble()
                val randomY = (0..screenHeight - effectSize).random().toDouble()
                spawnEffect(randomX, randomY, bitmap)
                delay(3000L)  // Adjust time for spawn interval
            }
        }
    }

    fun stopSpawning() {
        spawnerJob?.cancel()
    }

    private fun spawnEffect(x: Double, y: Double, bitmap: Bitmap) {
        val newEffect = SpriteEffect(
            position = XY(x, y),
            imagePath = bitmap,
            size = Size(effectSize, effectSize)
        )
        effects.add(newEffect)
        println("🧅 Spawned an onion at ($x, $y) - Total onions: ${effects.size}")
    }

    fun updateAll() {
        // Update decay for all effects
        val copy = effects.toList()
        copy.forEach { effect ->
            effect.updateDecay()  // Update decay time for each effect
            if (!effect.isAlive) {
                effects.remove(effect)  // Remove expired effects
            }
        }
    }
}