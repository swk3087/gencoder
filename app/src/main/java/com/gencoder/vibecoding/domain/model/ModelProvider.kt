package com.gencoder.vibecoding.domain.model

enum class ModelProvider(val displayName: String, val executableName: String) {
    GPT("GPT CLI", "gpt"),
    GEMINI("Gemini CLI", "gemini")
}
