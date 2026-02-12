package com.gencoder.vibecoding.util

import android.content.Context
import com.gencoder.vibecoding.domain.model.ModelProvider
import com.gencoder.vibecoding.domain.model.SessionConfig

class UserSessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun save(config: SessionConfig) {
        prefs.edit()
            .putString(KEY_USER, config.userName)
            .putString(KEY_WORKDIR, config.workingDirectory)
            .putString(KEY_PROVIDER, config.selectedProvider.name)
            .apply()
    }

    fun load(): SessionConfig {
        val provider = runCatching {
            ModelProvider.valueOf(prefs.getString(KEY_PROVIDER, ModelProvider.GPT.name).orEmpty())
        }.getOrDefault(ModelProvider.GPT)

        return SessionConfig(
            selectedProvider = provider,
            workingDirectory = prefs.getString(KEY_WORKDIR, "").orEmpty(),
            userName = prefs.getString(KEY_USER, "").orEmpty()
        )
    }

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_WORKDIR = "workdir"
        private const val KEY_PROVIDER = "provider"
    }
}
