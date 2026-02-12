package com.gencoder.vibecoding.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.gencoder.vibecoding.domain.model.ModelProvider

class SecureTokenStore(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        "secure_tokens",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(provider: ModelProvider, token: String) {
        prefs.edit().putString("token_${provider.name}", token).apply()
    }

    fun getToken(provider: ModelProvider): String {
        return prefs.getString("token_${provider.name}", "").orEmpty()
    }
}
