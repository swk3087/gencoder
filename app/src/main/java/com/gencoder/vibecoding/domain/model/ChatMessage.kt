package com.gencoder.vibecoding.domain.model

import java.time.Instant

data class ChatMessage(
    val id: Long,
    val provider: ModelProvider,
    val role: String,
    val content: String,
    val timestamp: Instant
)
