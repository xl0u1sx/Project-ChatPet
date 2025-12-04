package com.example.chatpet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText petNameInput;
    private RadioGroup petTypeRadioGroup;
    private RadioButton unicornRadio;
    private RadioButton dragonRadio;
    private Button createAccountButton;
    private Button backToLoginButton;
    private Button selectAvatarButton;
    private ImageView avatarPreview;
    private TextView errorText;
    private UserRepository userRepository;
    
    // Track selected pet type explicitly
    private String selectedPetType = "Unicorn"; // Default to Unicorn
    
    // Track avatar image
    private byte[] avatarImageData = null;
    
    // Activity result launcher for image picking
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //  views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        petNameInput = findViewById(R.id.petNameInput);
        petTypeRadioGroup = findViewById(R.id.petTypeRadioGroup);
        unicornRadio = findViewById(R.id.unicornRadio);
        dragonRadio = findViewById(R.id.dragonRadio);
        createAccountButton = findViewById(R.id.createAccountButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);
        selectAvatarButton = findViewById(R.id.selectAvatarButton);
        avatarPreview = findViewById(R.id.avatarPreview);
        errorText = findViewById(R.id.errorText);

        // Initialize repository
        userRepository = new UserRepository(this);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        handleImageSelection(imageUri);
                    }
                }
            }
        );

        // Set up radio button mutual exclusion manually
        // (needed because RadioButtons are nested in LinearLayouts)
        unicornRadio.setOnClickListener(v -> {
            selectedPetType = "Unicorn";
            unicornRadio.setChecked(true);
            dragonRadio.setChecked(false);
            Log.d(TAG, "Unicorn selected");
        });

        dragonRadio.setOnClickListener(v -> {
            selectedPetType = "Dragon";
            dragonRadio.setChecked(true);
            unicornRadio.setChecked(false);
            Log.d(TAG, "Dragon selected");
        });

        // select avatar button
        selectAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

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
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String petName = petNameInput.getText().toString().trim();

        // Use the tracked pet type variable
        String petType = selectedPetType;
        
        // Log for debugging
        Log.d(TAG, "Registration - Pet Type: " + petType);
        Log.d(TAG, "Dragon checked: " + dragonRadio.isChecked());
        Log.d(TAG, "Unicorn checked: " + unicornRadio.isChecked());

        // make sure no inputs are empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorText.setText("Please fill in all personal information fields");
            errorText.setVisibility(View.VISIBLE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            errorText.setText("Please enter a valid email address");
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

        // Create user object with avatar
        User newUser = new User(username, password, firstName, lastName, email, avatarImageData);

        // Register user
        Log.d(TAG, "Attempting to register user: " + username);
        boolean userCreated = userRepository.registerUser(newUser);

        if (userCreated) {
            Log.d(TAG, "User registered successfully, creating pet...");
            // create pet for user
            boolean petCreated = userRepository.createPetForUser(username, petName, petType);

            if (petCreated) {
                Log.d(TAG, "Pet created successfully!");
                // toast notification for login
                Toast.makeText(this, "Account created! Welcome, " + firstName + "!", Toast.LENGTH_LONG).show();

                // Go to pet meters page (PetActivity)
                Intent intent = new Intent(RegisterActivity.this, PetActivity.class);
                intent.putExtra(PetActivity.temp_user_id, username);
                intent.putExtra(PetActivity.temp_pet_name, petName);
                intent.putExtra(PetActivity.temp_pet_type, petType);
                startActivity(intent);
                finish(); // close activity
            } else {
                Log.e(TAG, "Failed to create pet for user: " + username);
                errorText.setText("Failed to create pet. Please try again.");
                errorText.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e(TAG, "Failed to register user: " + username);
            errorText.setText("Couldn't register. Please try again.");
            errorText.setVisibility(View.VISIBLE);
        }
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            // Load the image as a bitmap
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            if (bitmap != null) {
                // Resize the bitmap to a reasonable size (e.g., 300x300)
                Bitmap resizedBitmap = resizeBitmap(bitmap, 300, 300);
                
                // Display the image in the preview
                avatarPreview.setImageBitmap(resizedBitmap);
                
                // Convert to byte array for storage
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                avatarImageData = stream.toByteArray();
                
                Log.d(TAG, "Avatar image selected, size: " + avatarImageData.length + " bytes");
                Toast.makeText(this, "Avatar photo selected!", Toast.LENGTH_SHORT).show();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error selecting avatar image", e);
            Toast.makeText(this, "Error loading image. Please try another.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Validate email format
     * Checks for basic email pattern: text@text.text
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // Basic email pattern: must contain @ and . after @
        // Format: localpart@domain.extension
        String emailPattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        
        return email.matches(emailPattern);
    }
}
