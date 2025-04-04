package com.example.worstmixer.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.worstmixer.SpeelveldScreen // Ensure @Composable
import com.example.worstmixer.data.MenuItem
import com.example.worstmixer.data.WorstDataStore
import com.example.worstmixer.shop.Shop         // Ensure @Composable
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorstMixerScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("Worst Mixer") }
    val context = LocalContext.current
    val worstDataStore = remember { WorstDataStore(context) }
    var coins: Long by remember { mutableStateOf(0) }
    var highestworst: Int by remember { mutableStateOf(0) }
    LaunchedEffect(coins) {
        highestworst = getHighestWorstType(worstDataStore)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(title = title)
                DrawerBody(
                    items = listOf(
                        MenuItem(
                            id = "Worst Mixer",
                            title = "Worst Mixer",
                            contentDescription = "Worst Mixer",
                            icon = Icons.Filled.Home
                        ),
                        MenuItem(
                            id = "Shop",
                            title = "Shop",
                            contentDescription = "Shop",
                            icon = Icons.Filled.ShoppingCart
                        )
                    ),
                    onItemClick = { menuItem : MenuItem ->
                        title = menuItem.title
                        coroutineScope.launch {
                            drawerState.close()
                            snackbarHostState.showSnackbar("Selected: ${menuItem.title}")
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    actions = {
                        // Display coins in the TopAppBar
                        Text(text = "Coins: $coins", modifier = Modifier.padding(end = 16.dp))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Open navigation drawer")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxSize(),
            content = { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (title) {
                        "Worst Mixer" -> {SpeelveldScreen(worstDataStore,innerPadding, onCoinsUpdated = { newCoins ->
                            coins =+ newCoins // Update coins when the shop purchase happens
                            println("Coins updated in WorstMixerScreen: $coins")
                        })}
                        "Shop" -> {Shop(
                            worstDataStore = worstDataStore, onCoinsUpdated = { newCoins ->
                                coins = newCoins // Update coins when the shop purchase happens
                            },
                            highestworst = highestworst
                        )}
                        else -> Text("Unknown Screen")
                    }
                }
            }
        )
    }

}
suspend fun getHighestWorstType(worstDataStore: WorstDataStore): Int {
    val loadedWorsts = worstDataStore.loadGameState().first
    return loadedWorsts.maxByOrNull { it.worstType }?.worstType ?: 0
}