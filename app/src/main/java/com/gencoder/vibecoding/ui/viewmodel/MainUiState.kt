package com.gencoder.vibecoding.ui.viewmodel

import com.gencoder.vibecoding.domain.model.ChatMessage
import com.gencoder.vibecoding.domain.model.ModelProvider

data class MainUiState(
    val userName: String = "",
    val selectedProvider: ModelProvider = ModelProvider.GPT,
    val workingDirectory: String = "",
    val currentPrompt: String = "",
    val providerToken: String = "",
    val gitUrl: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val statusText: String = ""
)
