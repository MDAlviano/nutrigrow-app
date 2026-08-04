package com.nutrigrow.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore by preferencesDataStore(name = "auth_tokens")

class TokenManager(private val context: Context) {
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val getToken: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[JWT_TOKEN_KEY]
    }

    val getUserId: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    suspend fun saveAuthData(token: String, userId: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[JWT_TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun clearAuthData() {
        context.tokenDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}