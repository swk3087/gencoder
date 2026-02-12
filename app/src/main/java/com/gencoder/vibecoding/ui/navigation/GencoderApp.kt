package com.gencoder.vibecoding.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gencoder.vibecoding.ui.screens.MainScreen
import com.gencoder.vibecoding.ui.viewmodel.MainViewModel

@Composable
fun GencoderApp(context: Context) {
    val vm: MainViewModel = viewModel(factory = MainViewModel.factory(context))
    val state by vm.uiState.collectAsState()

    MainScreen(
        state = state,
        onUserNameChange = vm::updateUserName,
        onDirectoryChange = vm::updateWorkingDirectory,
        onProviderChange = vm::changeProvider,
        onTokenChange = vm::updateToken,
        onPromptChange = vm::updatePrompt,
        onGitUrlChange = vm::updateGitUrl,
        onSendPrompt = vm::sendPrompt,
        onClone = vm::cloneRepository
    )
}
