package com.gencoder.vibecoding.data.repo

import com.gencoder.vibecoding.domain.model.ModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CliExecutor {
    suspend fun sendPrompt(
        provider: ModelProvider,
        prompt: String,
        token: String,
        workingDirectory: String
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val process = ProcessBuilder(
                provider.executableName,
                "--prompt",
                prompt
            )
                .directory(workingDirectory.takeIf { it.isNotBlank() }?.let { File(it) })
                .apply {
                    if (token.isNotBlank()) {
                        environment()["API_TOKEN"] = token
                    }
                    redirectErrorStream(true)
                }
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                error("${provider.displayName} 실행 실패: $output")
            }
            output.ifBlank { "응답이 비어 있습니다." }
        }
    }
}
