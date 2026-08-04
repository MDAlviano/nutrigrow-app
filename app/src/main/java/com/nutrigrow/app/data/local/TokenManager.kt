package com.nutrigrow.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token_prefs")

class TokenManager(private val context: Context) {
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    val getToken: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[JWT_TOKEN_KEY]
    }

    val getUserId: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[USER_ID_KEY]
    }

    val getUserEmail: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY]
    }

    val getUserName: Flow<String?> = context.tokenDataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    suspend fun saveAuthData(token: String, userId: String = "", email: String = "", name: String = "") {
        context.tokenDataStore.edit { prefs ->
            prefs[JWT_TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_EMAIL_KEY] = email
            prefs[USER_NAME_KEY] = name
        }
    }

    suspend fun clearAuthData() {
        context.tokenDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}