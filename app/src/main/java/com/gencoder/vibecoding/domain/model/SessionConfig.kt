package com.gencoder.vibecoding.domain.model

data class SessionConfig(
    val selectedProvider: ModelProvider = ModelProvider.GPT,
    val workingDirectory: String = "",
    val userName: String = ""
)
