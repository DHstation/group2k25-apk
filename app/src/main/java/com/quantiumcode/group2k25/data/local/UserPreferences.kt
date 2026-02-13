package com.quantiumcode.group2k25.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    val userType: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_TYPE]
    }

    val lastPhone: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_PHONE]
    }

    suspend fun saveUserType(type: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_TYPE] = type
        }
    }

    suspend fun saveLastPhone(phone: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_PHONE] = phone
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_USER_TYPE = stringPreferencesKey("user_type")
        private val KEY_LAST_PHONE = stringPreferencesKey("last_phone")
    }
}
