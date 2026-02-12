package com.gencoder.vibecoding.data.repo

import com.gencoder.vibecoding.data.local.ChatDao
import com.gencoder.vibecoding.data.local.ChatMessageEntity
import com.gencoder.vibecoding.domain.model.ChatMessage
import com.gencoder.vibecoding.domain.model.ModelProvider
import java.time.Instant

class ChatRepository(private val dao: ChatDao) {
    suspend fun save(provider: ModelProvider, role: String, content: String) {
        dao.insertMessage(
            ChatMessageEntity(
                provider = provider.name,
                role = role,
                content = content,
                timestampEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun get(provider: ModelProvider): List<ChatMessage> {
        return dao.getMessagesForProvider(provider.name).map {
            ChatMessage(
                id = it.id,
                provider = ModelProvider.valueOf(it.provider),
                role = it.role,
                content = it.content,
                timestamp = Instant.ofEpochMilli(it.timestampEpochMs)
            )
        }
    }

    suspend fun clear(provider: ModelProvider) {
        dao.clearMessagesForProvider(provider.name)
    }
}
