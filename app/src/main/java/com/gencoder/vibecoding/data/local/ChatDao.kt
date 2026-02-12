package com.gencoder.vibecoding.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Insert
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("SELECT * FROM chat_messages WHERE provider = :provider ORDER BY timestampEpochMs ASC")
    suspend fun getMessagesForProvider(provider: String): List<ChatMessageEntity>

    @Query("DELETE FROM chat_messages WHERE provider = :provider")
    suspend fun clearMessagesForProvider(provider: String)
}
