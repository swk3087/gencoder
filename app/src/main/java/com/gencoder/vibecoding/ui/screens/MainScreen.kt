package com.gencoder.vibecoding.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gencoder.vibecoding.domain.model.ChatMessage
import com.gencoder.vibecoding.domain.model.ModelProvider
import com.gencoder.vibecoding.ui.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainUiState,
    onUserNameChange: (String) -> Unit,
    onDirectoryChange: (String) -> Unit,
    onProviderChange: (ModelProvider) -> Unit,
    onTokenChange: (String) -> Unit,
    onPromptChange: (String) -> Unit,
    onGitUrlChange: (String) -> Unit,
    onSendPrompt: () -> Unit,
    onClone: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Vibe Coding Assistant", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = state.userName,
            onValueChange = onUserNameChange,
            label = { Text("로그인 사용자 이름") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.workingDirectory,
            onValueChange = onDirectoryChange,
            label = { Text("작업 폴더 경로") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = state.selectedProvider.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("모델") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ModelProvider.entries.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(provider.displayName) },
                        onClick = {
                            expanded = false
                            onProviderChange(provider)
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.providerToken,
            onValueChange = onTokenChange,
            label = { Text("${state.selectedProvider.displayName} 토큰") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.currentPrompt,
            onValueChange = onPromptChange,
            label = { Text("프롬프트") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onSendPrompt, enabled = !state.isLoading) { Text("실행") }
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.height(20.dp))
        }

        OutlinedTextField(
            value = state.gitUrl,
            onValueChange = onGitUrlChange,
            label = { Text("Git Clone URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onClone, enabled = !state.isLoading) { Text("git clone") }

        Text("상태: ${state.statusText}", style = MaterialTheme.typography.bodySmall)

        Text("대화 기록 (${state.selectedProvider.displayName})", fontWeight = FontWeight.Bold)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.messages) { message ->
                MessageCard(message)
            }
        }
    }
}

@Composable
private fun MessageCard(message: ChatMessage) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = "${message.role.uppercase()} · ${message.timestamp}", style = MaterialTheme.typography.labelSmall)
        Text(text = message.content)
    }
}
