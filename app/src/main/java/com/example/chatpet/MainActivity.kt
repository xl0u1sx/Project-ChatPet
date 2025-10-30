package com.example.chatpet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.util.Log
import com.example.chatpet.ui.theme.ChatPetTheme // Ensure this import is correct

// Import for MainScreen if it's in the same file or package,
// otherwise, ensure it's imported from its correct location.
// Assuming MainScreen, LlmUiState are in this file or package for this example.

// UI related imports should ideally be within the Composable files that use them,
// but if MainScreen is in this file, they would be here.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState




class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()



        setContent {
            ChatPetTheme {
                MainScreen() // ViewModel will be automatically provided here by viewModel()
            }
        }
    }
}

// It's generally better to put Composable functions and related UI states
// in their own files for better organization, especially as the app grows.
// However, for a small example, they can be in the same file as the Activity.

// Note: LlmUiState is now defined in LlmUiState.java
// ChatViewModel is now defined in ChatViewModel.java

// MainScreen Composable (Handles the UI)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel() // Get instance of ChatViewModel
) {
    val context = LocalContext.current
    // Observe LiveData from Java ViewModel as Compose State
    val uiState: LlmUiState by chatViewModel.uiState.observeAsState(LlmUiState.Idle.INSTANCE as LlmUiState)

    var inputText by remember { mutableStateOf("") }

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pet Image at the top
            Image(
                painter = painterResource(id = R.drawable.unicorn), // Default to unicorn, could be made dynamic
                contentDescription = "Pet Image",
                modifier = Modifier
                    .size(120.dp)
                    .padding(top = 24.dp, bottom = 16.dp)
            )

            // Pet Response Area (takes up available space)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val state = uiState) {
                        is LlmUiState.Idle -> {
                            Text(
                                "Hi! I'm your pet companion. Ask me anything!",
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                            )
                        }
                        is LlmUiState.Loading -> {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Thinking...")
                        }
                        is LlmUiState.Success -> {
                            Text(
                                state.resultText,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                            )
                        }
                        is LlmUiState.Error -> {
                            Text(
                                "Oops! Something went wrong:",
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.errorMessage,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // User Input Area
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Chat with me!") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (uiState !is LlmUiState.Loading && inputText.isNotBlank()) {
                        Button(
                            onClick = {
                                val modelPath = context.getString(R.string.model_path)
                                val testPrompt = "You are a friendly pet companion named Daisy. Answer in a warm, caring way like a pet would."
                                chatViewModel.generateResponse(context, modelPath, inputText, testPrompt)
                                inputText = "" // Clear input after sending
                            }
                        ) {
                            Text("Send")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons at the bottom
            Button(
                onClick = {
                    val intent = Intent(context, JournalActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Pet Journal")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    val intent = Intent(context, PetActivity::class.java).apply {
                        putExtra(PetActivity.temp_user_id, "user123")         // TODO: replace with real user id
                        putExtra(PetActivity.temp_pet_type, "Unicorn")         // "Dragon" or "Unicorn"
                        putExtra(PetActivity.temp_pet_name, "Daisy")         // chosen name
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Pet Screen")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ChatPetTheme {
        // For preview, you might want to create a mock ViewModel
        // or just pass a default ViewModel which won't do network calls.
        // If your ChatViewModel has a default constructor, this will work.
        // Otherwise, you might need a custom preview that provides a mock.
        MainScreen()
    }
}