package com.example.worstmixer

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WorstView(
    context: Context,
    worst: Worst,
    screenWidth: Int,
    screenHeight: Int,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val scope = rememberCoroutineScope()
    // Create an Animatable for smooth dragging using an Offset and its vector converter.
    val animatableOffset = remember {
        Animatable(
            Offset(worst.position.x.toFloat(), worst.position.y.toFloat()),
            Offset.VectorConverter
        )
    }

    // When the Worst's position updates and it's not being dragged,
    // animate the animatableOffset toward the new position.
    LaunchedEffect(worst.position, worst.isDragging) {
        if (!worst.isDragging) {
            animatableOffset.animateTo(
                targetValue = Offset(worst.position.x.toFloat(), worst.position.y.toFloat()),
                animationSpec = tween(durationMillis = 100)
            )
        }
    }

    // Update loop for automatic movement using dynamic delta time.
    var lastTime by remember { mutableStateOf(System.nanoTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = System.nanoTime()
            val deltaTime = (currentTime - lastTime) / 1_000_000_000f // seconds
            lastTime = currentTime
            worst.update(context= context,deltaTime.toDouble(), mutableStateListOf(worst))
            delay(16L)
        }
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { change ->
                        // Start dragging for this Worst instance.
                        worst.handleDrag(true, change.x, change.y)
                    },
                    onDrag = { change, _ ->
                        worst.handleDrag(false, change.position.x, change.position.y)
                        // Launch a new coroutine to call snapTo.
                        scope.launch  {
                            animatableOffset.snapTo(
                                Offset(worst.position.x.toFloat(), worst.position.y.toFloat())
                            )
                        }
                    },
                    onDragEnd = {
                        worst.stopDragging()
                    }
                )
            }
    ) {
        drawIntoCanvas { canvas ->
            // Draw the worst's image at the smooth, animated offset.
            worst.image?.let {
                canvas.nativeCanvas.drawBitmap(
                    it,
                    animatableOffset.value.x,
                    animatableOffset.value.y,
                    null
                )
            }
        }
    }
}


