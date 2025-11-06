package com.example.chatpet

import android.content.Context
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
        const val PREFS_NAME = "ChatPetPrefs"
        const val KEY_USERNAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()

        // Save username to SharedPreferences if provided in intent
        val username = intent.getStringExtra("username")
        if (username != null) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_USERNAME, username).apply()
            Log.d(TAG, "Saved username to SharedPreferences: $username")
        }

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
    
    // Get username from SharedPreferences (persists across activity recreations)
    val username = remember {
        if (context is MainActivity) {
            val prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val savedUsername = prefs.getString(MainActivity.KEY_USERNAME, null)
            val intentUsername = context.intent.getStringExtra("username")
            
            // Use intent username if available, otherwise use saved username, or default
            val finalUsername = intentUsername ?: savedUsername ?: "user123"
            Log.d("MainActivity", "Using username: $finalUsername (intent: $intentUsername, saved: $savedUsername)")
            finalUsername
        } else {
            "user123"
        }
    }
    
    // Use mutableState to allow refreshing pet info
    var petInfo by remember { mutableStateOf<UserRepository.PetInfo?>(null) }
    
    // Refresh pet info when the composable is first created and when resumed
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val userRepository = UserRepository(context)
                petInfo = userRepository.getPetInfo(username)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // Determine which pet image to display based on type and level
    val petImageRes = petInfo?.let { info ->
        when (info.petType) {
            "Dragon" -> {
                when (info.petLevel) {
                    1 -> R.drawable.dragon_level1
                    2 -> R.drawable.dragon_level2
                    3 -> R.drawable.dragon_level3
                    else -> R.drawable.dragon_level1
                }
            }
            else -> { // Unicorn
                when (info.petLevel) {
                    1 -> R.drawable.unicorn_level1
                    2 -> R.drawable.unicorn_level2
                    3 -> R.drawable.unicorn_level3
                    else -> R.drawable.unicorn_level1
                }
            }
        }
    } ?: R.drawable.unicorn_level1 // Default image if petInfo is null

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pet Image at the top - now dynamic based on user's pet
            Image(
                painter = painterResource(id = petImageRes),
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
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD1DC), Color(0xFFFFD1DC))),
                            shape = RoundedCornerShape(10.dp)
                ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (val state = uiState) {
                        is LlmUiState.Idle -> {
                            val petName = petInfo?.petName ?: "your pet"
                            val petType = petInfo?.petType ?: "companion"
                            Text(
                                "Hi! I'm $petName, your $petType companion. Ask me anything!",
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
                                val petName = petInfo?.petName ?: "Daisy"
                                val petType = petInfo?.petType ?: "Unicorn"
                                val testPrompt = "You are a friendly $petType companion named $petName. Answer in a warm, caring way like a $petType pet would."
                                chatViewModel.generateResponse(context, modelPath, inputText, testPrompt)
                                
                                // Increase happiness by 15 when sending a chat
                                if (context is MainActivity) {
                                    val prefs = context.getSharedPreferences("PetActivityPrefs", Context.MODE_PRIVATE)
                                    val currentHappiness = prefs.getInt(username + "_happiness", 30)
                                    val newHappiness = Math.min(100, currentHappiness + 15)
                                    prefs.edit()
                                        .putInt(username + "_happiness", newHappiness)
                                        .putLong(username + "_lastSave", System.currentTimeMillis())
                                        .apply()
                                    Log.d("MainActivity", "Increased happiness for $username from $currentHappiness to $newHappiness")
                                }
                                
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
                modifier = Modifier
                    .width(200.dp)
                    .height(70.dp)
                    .padding(start=10.dp, top=10.dp)
                    .background(
                        brush=Brush.linearGradient(
                            colors=listOf(Color(0xFFFFB6C1), Color(0xFFFFD1DC))
                        ),
                        shape=RoundedCornerShape(190.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(190.dp)
                    ),
                colors= ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(190.dp),
                contentPadding = PaddingValues(0.dp)
            )
                 {
                    Text(text = "PET JOURNAL",
                        color = Color(0xFF3E2723),
                        fontSize = 16.sp)

                }

            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    // Use the username from the outer scope (already loaded from SharedPreferences)
                    // Fetch pet information from database
                    val userRepository = UserRepository(context)
                    val petInfoForIntent = userRepository.getPetInfo(username)
                    
                    val intent = Intent(context, PetActivity::class.java).apply {
                        putExtra(PetActivity.temp_user_id, username)
                        putExtra(PetActivity.temp_pet_type, petInfoForIntent?.petType ?: "Unicorn")
                        putExtra(PetActivity.temp_pet_name, petInfoForIntent?.petName ?: "Daisy")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(70.dp)
                    .padding(start=10.dp, top=10.dp)
                    .background(
                        brush=Brush.linearGradient(
                            colors=listOf(Color(0xFFFFB6C1), Color(0xFFFFD1DC))
                        ),
                        shape=RoundedCornerShape(190.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(190.dp)
                    ),

                colors= ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(190.dp),
                contentPadding = PaddingValues(0.dp)
            )

            {
                Text(text="PET SCREEN",
                    color = Color(0xFF3E2723),
                    fontSize = 16.sp)
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