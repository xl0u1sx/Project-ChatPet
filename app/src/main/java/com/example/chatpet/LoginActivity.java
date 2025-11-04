package com.example.chatpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private TextView errorText;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        errorText = findViewById(R.id.errorText);

        // Initialize repo
        userRepository = new UserRepository(this);

        // Set up login button
        loginButton.setOnClickListener(v -> handleLogin());

        // Set up register button
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // point to register activity
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // don't allow empty password or username
        if (username.isEmpty() || password.isEmpty()) {
            String message = "Please enter both username and password";
            errorText.setText(message);
            errorText.setVisibility(View.VISIBLE);
        
            return;
        }

        // remove error if there was previously an error
        errorText.setVisibility(View.GONE);

        // try to authenticate with repo
        User user = userRepository.authenticate(username, password);

        if (user != null) {
            // android toast notification
            // https://developer.android.com/guide/topics/ui/notifiers/toasts
            Toast.makeText(this, "Welcome back, " + user.getFirstName() + "!", Toast.LENGTH_SHORT).show();

            // go to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            // auth failed
            String message = "User not found. Please check details again or create an account.";
            errorText.setText(message);
            errorText.setVisibility(View.VISIBLE);
        }
    }


    
}
