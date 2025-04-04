    // File: SpeelveldScreen.kt
    package com.example.worstmixer

    import android.content.Context
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.media.MediaPlayer
    import android.util.Size
    import androidx.compose.foundation.Canvas
    import androidx.compose.foundation.gestures.detectDragGestures
    import androidx.compose.foundation.layout.PaddingValues
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.padding
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
    import androidx.compose.ui.graphics.nativeCanvas
    import androidx.compose.ui.input.pointer.pointerInput
    import androidx.compose.ui.platform.LocalConfiguration
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.platform.LocalDensity
    import androidx.compose.ui.unit.LayoutDirection
    import androidx.compose.ui.unit.dp
    import androidx.core.graphics.drawable.toBitmap
    import com.example.worstmixer.data.WorstDataStore
    import kotlinx.coroutines.*
    import kotlin.random.Random


    @Composable
    fun SpeelveldScreen(worstDataStore: WorstDataStore,innerPadding: PaddingValues, onCoinsUpdated: (Long) -> Unit ) {
        val context = LocalContext.current
        val worstSize = 160
        val displayMetrics = context.resources.displayMetrics
        val pixelwidth = displayMetrics.widthPixels
        val pixelheight = displayMetrics.heightPixels
        val screenWidth = pixelwidth - (innerPadding.calculateLeftPadding(LayoutDirection.Ltr).value.toInt()) -
                (innerPadding.calculateRightPadding(LayoutDirection.Ltr).value.toInt())
        val screenHeight = pixelheight - (innerPadding.calculateTopPadding().value.toInt()) -
                (innerPadding.calculateBottomPadding().value.toInt())


        var holyworst by remember { mutableStateOf(false) }
        // Background music
        val backgroundMusic = remember {
            MediaPlayer.create(context, R.raw.backgroundmusic).apply {
                isLooping = true
                start()
            }
        }

        // Holy music player
        var holyMusicPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
        val a: Long = 1
        var currentCoins by remember { mutableStateOf(a) }
        var mergeCounter by remember { mutableStateOf(0) }

        val spawner = remember {
            WorstSpawner(
                screenWidth = screenWidth, screenHeight = screenHeight, worstSize = worstSize, onMergeHappened = {
                mergeCounter++
            }, holyworst = {
                holyworst = true
            },
                onCoinsUpdated = {
                    currentCoins += it
                    onCoinsUpdated(currentCoins)
                    println("Coins updated: $currentCoins")
                }
            )
        }

        val backgroundImage = remember {
            BitmapFactory.decodeResource(context.resources, R.drawable.background)
        }

        fun stopBackgroundMusic() {
            if (backgroundMusic.isPlaying) {
                backgroundMusic.stop()
                backgroundMusic.release()
            }
        }

        fun playRandomHolyMusic() {
            val holyTracks = listOf(R.raw.epic1, R.raw.epic2)
            val randomTrack = holyTracks.random()

            holyMusicPlayer?.release() // Voorkomt geheugenlekken
            holyMusicPlayer = MediaPlayer.create(context, randomTrack).apply {
                isLooping = false
                start()
                setOnCompletionListener {
                    playRandomHolyMusic() // Speel automatisch een nieuw nummer als het eindigt
                }
            }
        }

        // Effect: wanneer holyworst spawnt
        LaunchedEffect(holyworst) {
            if (holyworst) {
                stopBackgroundMusic()
                playRandomHolyMusic()
            }
        }

        // Load game state safely in a LaunchedEffect
        LaunchedEffect(Unit) {
            val loadedWorsts = worstDataStore.loadGameState().first
            val loadedCoins = worstDataStore.loadGameState().second
            val newWorsts = mutableListOf<Worst>()
            var highestWorstType = 0
            currentCoins = loadedCoins
            loadedWorsts.forEach { worst ->
                if (worst.position.x == 0.0 || worst.position.y == 0.0) {
                    worst.resetPositionIfOffScreen()
                }
                newWorsts.add(worst)
            }

            loadedWorsts.forEach { worst ->
                worst.image = getImageForTypeWorst(context, worst.worstType, Size(worstSize, worstSize))
                newWorsts.add(worst)
                if (worst.worstType > highestWorstType) {
                    highestWorstType = worst.worstType
                }
            }

            // Update state safely after all iterations are done
            spawner.worsts.clear()
            spawner.worsts.addAll(newWorsts)

            currentCoins = loadedCoins
            spawner.startSpawning()
        }

        // Save game state whenever mergeCounter changes (side effect)
        LaunchedEffect(mergeCounter) {
            withContext(Dispatchers.IO) {
                worstDataStore.saveGameState(spawner.worsts, currentCoins)
            }
        }
        LaunchedEffect(currentCoins) {
            withContext(Dispatchers.IO) {
                worstDataStore.saveGameState(spawner.worsts, currentCoins)
            }
        }
        // Game loop to update state (e.g., movement, interactions)
        var lastTime by remember { mutableStateOf(System.nanoTime()) }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                while (true) {
                    val currentTime = System.nanoTime()
                    val deltaTime = (currentTime - lastTime) / 1_000_000_000f
                    lastTime = currentTime
                    spawner.updateAll(context, deltaTime.toDouble())  // Update all the worsts
                    delay(16L)  // ~60 FPS update rate
                }
            }
        }

        var selectedWorst by remember { mutableStateOf<Worst?>(null) }

        // Canvas and pointer gestures for dragging
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { change ->
                            val touchRadius = 1000.0
                            selectedWorst = spawner.worsts
                                .filter {
                                    val dx = it.position.x - change.x.toDouble()
                                    val dy = it.position.y - change.y.toDouble()
                                    dx * dx + dy * dy <= touchRadius * touchRadius
                                }
                                .minByOrNull {
                                    val dx = it.position.x - change.x.toDouble()
                                    val dy = it.position.y - change.y.toDouble()
                                    dx * dx + dy * dy
                                }
                            selectedWorst?.handleDrag(true, change.x, change.y)
                        },
                        onDrag = { change, _ ->
                            selectedWorst?.handleDrag(false, change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            selectedWorst?.stopDragging()
                            selectedWorst = null
                        }
                    )
                }
        ) {
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawBitmap(backgroundImage, 0f, 0f, null)
            }
            val worstListCopy = spawner.worsts.toList()
            worstListCopy.forEach { worst ->
                worst.image?.let { bmp ->
                    drawIntoCanvas { canvas ->
                        val paint = worst.applyOpacity()  // Apply opacity logic
                        canvas.nativeCanvas.drawBitmap(
                            bmp,
                            worst.position.x.toFloat(),
                            worst.position.y.toFloat(),
                            paint  // Apply the Paint object
                        )
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                backgroundMusic.stop()
                backgroundMusic.release()
                holyMusicPlayer?.stop()
                holyMusicPlayer?.release()
            }
        }
    }

    fun getImageForTypeWorst(context: Context, type: Int, size: Size): Bitmap? {
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

