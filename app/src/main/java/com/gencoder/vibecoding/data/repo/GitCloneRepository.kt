package com.gencoder.vibecoding.data.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GitCloneRepository {
    suspend fun clone(repoUrl: String, destinationRoot: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            require(repoUrl.startsWith("http")) { "HTTP(S) Git URL만 지원합니다." }
            require(destinationRoot.isNotBlank()) { "저장할 폴더를 입력하세요." }

            val destinationFile = File(destinationRoot)
            require(destinationFile.exists() && destinationFile.isDirectory) { "유효한 폴더를 선택하세요." }

            val repoName = repoUrl.substringAfterLast('/').removeSuffix(".git").ifBlank { "cloned_repo" }
            val target = File(destinationFile, repoName)
            if (target.exists()) {
                error("이미 동일한 폴더가 존재합니다: ${target.absolutePath}")
            }

            val process = ProcessBuilder("git", "clone", repoUrl, target.absolutePath)
                .directory(destinationFile)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                error("git clone 실패: $output")
            }
            "clone 완료: ${target.absolutePath}"
        }
    }
}
