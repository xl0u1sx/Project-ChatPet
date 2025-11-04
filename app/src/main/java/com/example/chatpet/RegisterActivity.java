package com.example.chatpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText petNameInput;
    private RadioGroup petTypeRadioGroup;
    private RadioButton unicornRadio;
    private RadioButton dragonRadio;
    private Button createAccountButton;
    private Button backToLoginButton;
    private TextView errorText;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //  views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        petNameInput = findViewById(R.id.petNameInput);
        petTypeRadioGroup = findViewById(R.id.petTypeRadioGroup);
        unicornRadio = findViewById(R.id.unicornRadio);
        dragonRadio = findViewById(R.id.dragonRadio);
        createAccountButton = findViewById(R.id.createAccountButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);
        errorText = findViewById(R.id.errorText);

        // Initialize repository
        userRepository = new UserRepository(this);

        // create account buttong
        createAccountButton.setOnClickListener(v -> handleRegistration());

        // back to login buttong
        backToLoginButton.setOnClickListener(v -> {
            finish(); // close activity
        });
    }

    private void handleRegistration() {
        // get inputs
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String petName = petNameInput.getText().toString().trim();

        // Get selected pet type
        int selectedPetTypeId = petTypeRadioGroup.getCheckedRadioButtonId();
        String petType;
        if (selectedPetTypeId == R.id.unicornRadio) {
            petType = "Unicorn";
        } else {
            petType = "Dragon";
        }

        // make sure no inputs are empty
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorText.setText("Please fill in all personal information fields");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (petName.isEmpty()) {
            errorText.setText("Please give your pet a name");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        if (password.length() < 4) {
            errorText.setText("Password must be at least 4 characters long");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        // hide error if shown
        errorText.setVisibility(View.GONE);

        // Check if username already exists
        if (userRepository.userExists(username)) {
            errorText.setText("Username already exists. Please choose a different username.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        // Create user object
        User newUser = new User(username, password, firstName, lastName);

        // Register user
        boolean userCreated = userRepository.registerUser(newUser);

        if (userCreated) {
            // create pet for user
            boolean petCreated = userRepository.createPetForUser(username, petName, petType);

            if (petCreated) {
                // toast notification for login
                Toast.makeText(this, "Account created! Welcome, " + firstName + "!", Toast.LENGTH_LONG).show();

                // go to main activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish(); // close activity
            } else {
                errorText.setText("Failed to create pet. Please try again.");
                errorText.setVisibility(View.VISIBLE);
            }
        } else {
            errorText.setText("Couldn't register. try again");
            errorText.setVisibility(View.VISIBLE);
        }
    }
}
