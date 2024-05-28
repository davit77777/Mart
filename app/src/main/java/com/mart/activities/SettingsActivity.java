package com.mart.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mart.R;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private TextInputLayout textInputLayout;
    private EditText textInputEditText;
    private Button saveButton;
    private SwitchMaterial leaderboardSwitch;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userDocRef = db.collection("users").document(user.getUid());

        textInputLayout = findViewById(R.id.outlined_TextField_name);
        textInputEditText = textInputLayout.getEditText();
        saveButton = findViewById(R.id.saveButton);
        leaderboardSwitch = findViewById(R.id.leaderboardSwitch);

        fetchUserData();

        leaderboardSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility(isChecked));

        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void fetchUserData() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                Boolean visibility = documentSnapshot.getBoolean("visibility");

                if (username != null) {
                    textInputEditText.setText(username);
                }
                if (visibility != null) {
                    leaderboardSwitch.setChecked(visibility);
                }
            }
        });
    }

    private void updateVisibility(boolean isChecked) {
        Map<String, Object> visibilityUpdate = new HashMap<>();
        visibilityUpdate.put("visibility", isChecked);
        userDocRef.set(visibilityUpdate, SetOptions.merge());
    }

    private void saveUserData() {
        String username = textInputEditText.getText().toString().trim();
        boolean displayInLeaderboard = leaderboardSwitch.isChecked();

        if (username.isEmpty()) {
            textInputLayout.setError("Username cannot be empty");
            return;
        }

        textInputLayout.setError(null);

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("visibility", displayInLeaderboard);

        userDocRef.set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(SettingsActivity.this, "Settings saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SettingsActivity.this, "Error saving settings", Toast.LENGTH_SHORT).show());
    }
}
