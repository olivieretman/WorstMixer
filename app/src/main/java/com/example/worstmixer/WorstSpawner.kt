// File: WorstSpawner.kt
package com.example.worstmixer

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.*
import kotlin.random.Random

class WorstSpawner(
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val worstSize: Int,
    private val onMergeHappened: () -> Unit,
    private val holyworst: () -> Unit,
    var onCoinsUpdated: (Long) -> Unit
) {
    val worsts = mutableStateListOf<Worst>()
    private var spawnerJob: Job? = null
    private var spawnDelay = 5000L  // Initial delay
    var lastMergeTime: Long = 0L
    private val mergeCooldown = 300L
    fun getHighestType(): Int {
        return worsts.maxOfOrNull { it.worstType } ?: 1
    }

    fun startSpawning() {
        spawnerJob?.cancel()
        spawnerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val (x, y) = generateRandomSpawnPosition()
                spawnWorst(x, y)
                delay(spawnDelay)
                adjustSpawnRate()
            }
        }
    }

    fun stopSpawning() {
        spawnerJob?.cancel()
    }

    private fun generateRandomSpawnPosition(): Pair<Double, Double> {
        val x = (0..screenWidth - worstSize).random().toDouble()
        val y = (0..screenHeight - worstSize).random().toDouble()
        return Pair(x, y)
    }
    fun notTooManyWorstsOnScreen(): Boolean {
        return worsts.size >= 20
    }
    private fun spawnWorst(x: Double, y: Double) {
        if(notTooManyWorstsOnScreen()) return
        val highestType = getHighestType()
        val newType = getRandomSpawnType(highestType)

        val newWorst = Worst(
            initialPosition = XY(x, y),
            size = Size(worstSize, worstSize),
            image = null,  // Loaded dynamically elsewhere
            worstType = newType,
            income = calculateIncome(newType),
            SCREEN_WIDTH = screenWidth,
            SCREEN_HEIGHT = screenHeight,
            onMerged = onMergeHappened,
            holyworst = holyworst,
            onCoinsUpdated = { coins ->
                onCoinsUpdated(coins)
            }
        ).apply {
            velocity = XY((3..6).random().toDouble(), (3..6).random().toDouble())
        }

        worsts.add(newWorst)
    }

    private fun getRandomSpawnType(highestType: Int): Int {
        val weights = mutableMapOf<Int, Int>()

        // Assign probabilities
        for (i in 1..highestType) {
            weights[i] = when {
                i == highestType -> 1  // 1% chance for highest unlocked worst
                i == highestType - 1 -> 5  // 5% for second highest
                i == highestType - 2 -> 10  // 10% for third highest
                else -> 40  // 40% for lower tier
            }
        }

        // Normalize weights
        val totalWeight = weights.values.sum()
        val randomValue = Random.nextInt(1, totalWeight + 1)
        var cumulative = 0

        for ((type, weight) in weights) {
            cumulative += weight
            if (randomValue <= cumulative) {
                return type
            }
        }

        return 1  // Fallback
    }
    fun canMerge(): Boolean {
        return (System.currentTimeMillis() - lastMergeTime) >= mergeCooldown
    }
    private fun calculateIncome(type: Int): Long {
        return when (type) {
            1 -> 10
            2 -> 25
            3 -> 50
            4 -> 100
            5 -> 250
            else -> 500
        }
    }

    private fun adjustSpawnRate() {
        val highestType = getHighestType()
        spawnDelay = when {
            highestType >= 10 -> 2500L
            highestType >= 5 -> 3500L
            else -> 5000L
        }
    }




    fun updateAll(context: Context, deltaTime: Double) {
        // Create a snapshot copy of the list to avoid concurrent modification
        val tempWorsts = worsts.toList()
        // Create a temporary list to store items that need to be added or updated
        val newItems = mutableListOf<Worst>()

         tempWorsts.forEach { worst ->
            // Update the individual worst
            worst.update(context, deltaTime, worsts)

            // Check if a merge condition is met (collision)
             val worstsSnapshot = worsts.toList()  // Create a snapshot of the list
             val collidedWorst = worstsSnapshot.find { it !== worst && worst.collidesWith(it) }
            if (collidedWorst != null && canMerge()) {
                worst.reset()  // Reset the worst before merging
                worst.mergeWith(collidedWorst, worsts as MutableList<Worst>, context)
            }
        }

        // After iteration, apply any changes like new items or updates (if necessary)
        // You can also perform other logic or merging after iteration here if needed
    }


}

