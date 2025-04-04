package com.example.worstmixer.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.worstmixer.Worst
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer

val Context.dataStore by preferencesDataStore(name = "worstmixer_store")

class WorstDataStore(private val context: Context) {
    companion object {
        private val COINS_KEY  = longPreferencesKey("coins")
        private val WORSTS_KEY = stringPreferencesKey("worsts")
    }

    /**
     * Saves the game state: list of Worst objects (converted to WorstSerializable) and coins.
     */
    suspend fun saveGameState(worsts: List<Worst>, coins: Long) {
        val worstsJson = Json.encodeToString(worsts.map { it.toSerializable() })
        context.dataStore.edit { prefs ->
            prefs[COINS_KEY] = coins
            prefs[WORSTS_KEY] = worstsJson
        }
    }

    /**
     * Loads the game state: returns a Pair of list of Worst objects and coin balance.
     */
    suspend fun loadGameState(): Pair<List<Worst>, Long> {
        val prefs = context.dataStore.data.first()
        val coins = prefs[COINS_KEY] ?: 0
        val worstsJson = prefs[WORSTS_KEY] ?: "[]"
        val worstSerializable = Json.decodeFromString(ListSerializer<WorstSerializable>(WorstSerializable.serializer()), worstsJson)
        val worsts = worstSerializable.map { it.toWorst() }
        return Pair(worsts, coins)
    }
}