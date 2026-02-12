package com.gencoder.vibecoding.domain.usecase

import com.gencoder.vibecoding.data.repo.ChatRepository
import com.gencoder.vibecoding.data.repo.CliExecutor
import com.gencoder.vibecoding.domain.model.ModelProvider

class SendPromptUseCase(
    private val cliExecutor: CliExecutor,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        provider: ModelProvider,
        prompt: String,
        token: String,
        workingDirectory: String
    ): Result<String> {
        chatRepository.save(provider, "user", prompt)
        val response = cliExecutor.sendPrompt(provider, prompt, token, workingDirectory)
        response.onSuccess { chatRepository.save(provider, "assistant", it) }
        return response
    }
}
