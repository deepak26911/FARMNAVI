package com.example.farmnavi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp_Activity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginText: TextView

    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        backButton = findViewById(R.id.backButton)
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        locationInput = findViewById(R.id.locationInput)
        passwordInput = findViewById(R.id.passwordInput)
        signUpButton = findViewById(R.id.signUpButton)
        loginText = findViewById(R.id.loginText)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        backButton.setOnClickListener {
            onBackPressed()
        }

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val location = locationInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User created, get user ID
                        val userId = auth.currentUser?.uid

                        // Prepare user data
                        val userMap = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "location" to location
                        )

                        // Save user data to Firestore under "users" collection
                        if (userId != null) {
                            firestore.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Sign Up successful!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // Navigate to MainActivity
                                    val intent = Intent(this, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish() // close SignUpActivity
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Failed to save user data: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        val errorMsg = task.exception?.message ?: "Sign Up failed."
                        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
        }

        loginText.setOnClickListener {
            // Navigate to login screen
            // TODO: Implement navigation, e.g.,
            startActivity(Intent(this, Login_Activity::class.java))
            Toast.makeText(this, "Navigate to Login screen", Toast.LENGTH_SHORT).show()
        }
    }
}
