package com.gencoder.vibecoding.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gencoder.vibecoding.data.local.GencoderDatabase
import com.gencoder.vibecoding.data.repo.ChatRepository
import com.gencoder.vibecoding.data.repo.CliExecutor
import com.gencoder.vibecoding.data.repo.GitCloneRepository
import com.gencoder.vibecoding.domain.model.ModelProvider
import com.gencoder.vibecoding.domain.model.SessionConfig
import com.gencoder.vibecoding.domain.usecase.SendPromptUseCase
import com.gencoder.vibecoding.util.SecureTokenStore
import com.gencoder.vibecoding.util.UserSessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val sendPromptUseCase: SendPromptUseCase,
    private val chatRepository: ChatRepository,
    private val gitCloneRepository: GitCloneRepository,
    private val tokenStore: SecureTokenStore,
    private val sessionStore: UserSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val session = sessionStore.load()
        _uiState.value = _uiState.value.copy(
            userName = session.userName,
            selectedProvider = session.selectedProvider,
            workingDirectory = session.workingDirectory,
            providerToken = tokenStore.getToken(session.selectedProvider)
        )
        loadMessages(session.selectedProvider)
    }

    fun updateUserName(name: String) = updateAndPersist { it.copy(userName = name) }
    fun updateWorkingDirectory(directory: String) = updateAndPersist { it.copy(workingDirectory = directory) }
    fun updatePrompt(prompt: String) = _uiState.value.let { _uiState.value = it.copy(currentPrompt = prompt) }
    fun updateToken(token: String) = _uiState.value.let { _uiState.value = it.copy(providerToken = token) }
    fun updateGitUrl(url: String) = _uiState.value.let { _uiState.value = it.copy(gitUrl = url) }

    fun changeProvider(provider: ModelProvider) {
        _uiState.value = _uiState.value.copy(
            selectedProvider = provider,
            providerToken = tokenStore.getToken(provider)
        )
        persistSession()
        loadMessages(provider)
    }

    fun sendPrompt() {
        val state = _uiState.value
        if (state.currentPrompt.isBlank()) return

        tokenStore.saveToken(state.selectedProvider, state.providerToken)
        _uiState.value = state.copy(isLoading = true, statusText = "${state.selectedProvider.displayName} 호출 중...")

        viewModelScope.launch {
            val result = sendPromptUseCase(
                provider = state.selectedProvider,
                prompt = state.currentPrompt,
                token = state.providerToken,
                workingDirectory = state.workingDirectory
            )

            result.onSuccess {
                loadMessages(state.selectedProvider)
                _uiState.value = _uiState.value.copy(
                    currentPrompt = "",
                    isLoading = false,
                    statusText = "응답 완료"
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusText = it.message ?: "실패"
                )
            }
        }
    }

    fun cloneRepository() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, statusText = "git clone 실행 중...")
            val result = gitCloneRepository.clone(state.gitUrl, state.workingDirectory)
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, statusText = it) },
                onFailure = { _uiState.value.copy(isLoading = false, statusText = it.message ?: "clone 실패") }
            )
        }
    }

    private fun loadMessages(provider: ModelProvider) {
        viewModelScope.launch {
            val messages = chatRepository.get(provider)
            _uiState.value = _uiState.value.copy(messages = messages)
        }
    }

    private fun updateAndPersist(update: (MainUiState) -> MainUiState) {
        _uiState.value = update(_uiState.value)
        persistSession()
    }

    private fun persistSession() {
        sessionStore.save(
            SessionConfig(
                selectedProvider = _uiState.value.selectedProvider,
                userName = _uiState.value.userName,
                workingDirectory = _uiState.value.workingDirectory
            )
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dao = GencoderDatabase.getInstance(context).chatDao()
                    val chatRepo = ChatRepository(dao)
                    return MainViewModel(
                        sendPromptUseCase = SendPromptUseCase(CliExecutor(), chatRepo),
                        chatRepository = chatRepo,
                        gitCloneRepository = GitCloneRepository(),
                        tokenStore = SecureTokenStore(context),
                        sessionStore = UserSessionStore(context)
                    ) as T
                }
            }
    }
}
