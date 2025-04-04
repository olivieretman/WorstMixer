package com.example.worstmixer.shop

import android.graphics.Bitmap
import android.util.Size
import androidx.compose.runtime.Composable


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.worstmixer.Worst
import com.example.worstmixer.XY
import com.example.worstmixer.data.WorstDataStore
import kotlinx.coroutines.launch

@Composable
fun Shop(worstDataStore: WorstDataStore, onCoinsUpdated: (Long) -> Unit, highestworst: Int) {
    val coroutineScope = rememberCoroutineScope()
    var coins: Long by remember { mutableStateOf(0) }
    var purchasedWorstList by remember { mutableStateOf<List<Worst>>(emptyList()) }

    // Load saved game state (coins and purchased worsts)
    LaunchedEffect(Unit) {
        val (worsts, savedCoins) = worstDataStore.loadGameState()
        coins = savedCoins
        purchasedWorstList = worsts
    }
    val availableItems = listOf(
        if (highestworst > 1) shopItemMaker("Cocktailworst", "cocktailworst", 20, 1)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 2) shopItemMaker("Hotdog", "hotdog", 40, 2)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 3) shopItemMaker("Weiswurst", "weiswurst", 160, 3)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 4) shopItemMaker("Frikandel", "frikandel", 2560, 4)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 5) shopItemMaker("Braatworst", "braatworst", 55000, 5)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 6) shopItemMaker("HongaarsWorstje", "hongaarsworst", 650000, 6)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 7) shopItemMaker("Bloodworst", "bloodworst", 3650000, 7)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 8) shopItemMaker("Sausijs", "sausijs", 42949670, 8)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 9) shopItemMaker("HeiligWorstje", "heiligworstje", 42949670 * 10, 9)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 10) shopItemMaker("mummyworst", "mummyworst", 42949670 * 20, 10)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 11) shopItemMaker("barbieworst", "barbieworst", 42949670 * 40, 11)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 12) shopItemMaker("bananaworst", "bananaworst", 42949670 * 80, 12)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 13) shopItemMaker("suitworst", "suitworst", 42949670 * 160, 13)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 14) shopItemMaker("darthvaderworst", "darthvaderworst", 42949670 * 320, 14)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 15) shopItemMaker("rookworst", "rookworst", 42949670 * 640, 15)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 16) shopItemMaker("koreanworst", "koreanworst", 42949670 * 1280, 16)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),
        if (highestworst > 17) shopItemMaker("superworst", "superworst", 42949670 * 2560, 17)else shopItemMaker("Cocktailworst", "cocktailworst", 20, 1),

        )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Coins: $coins", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = availableItems.size) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(text = availableItems[item].name, modifier = Modifier.weight(1f))
                    Text(text = "Price: ${availableItems[item].price}", modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (coins >= availableItems[item].price.toLong()) {
                                coins -= availableItems[item].price.toLong()
                                purchasedWorstList = purchasedWorstList + availableItems[item].worst
                                coroutineScope.launch {
                                    // Save the new state to the data store
                                    worstDataStore.saveGameState(purchasedWorstList, coins)
                                    onCoinsUpdated(coins)  // Notify the parent composable to update coins
                                }
                            } else {
                                // Optionally, show a message if the user doesn't have enough coins
                                println("Not enough coins!")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Buy")
                    }
                }
            }
        }
    }
}

fun shopItemMaker(name: String, id: String, price: Int, worstType: Int): ShopItem {
    return ShopItem(id, name, price, Worst(
        initialPosition = generateRandomSpawnPosition(),
        size = Size(160, 160),
        image = null,
        worstType = worstType,
        income = 2,
        SCREEN_WIDTH = 1080,
        SCREEN_HEIGHT = 1920,
        onMerged = null,
        holyworst = null,
        isVisible = true,
        opacity = 1.0f,
        lastSparkleTime = System.currentTimeMillis(),
        velocity = XY(3.0, 3.0),
        onCoinsUpdated = null
    ))
}
fun generateRandomSpawnPosition(): XY{
    return XY( (0..1080 - 160).random().toDouble(), (0..1920 - 160).random().toDouble())
}


