package com.example.farmnavi

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login_Activity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var togglePasswordButton: ImageButton
    private lateinit var forgotPasswordText: TextView
    private lateinit var signInButton: Button
    private lateinit var signUpText: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        auth = FirebaseAuth.getInstance()

        backButton = findViewById(R.id.backButton)
        emailInput = findViewById(R.id.emailPhoneInput)
        passwordInput = findViewById(R.id.passwordInput)
        togglePasswordButton = findViewById(R.id.togglePassword)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
        signInButton = findViewById(R.id.signInButton)
        signUpText = findViewById(R.id.signUpText)

        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set keyboard type for email input explicitly
        emailInput.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        togglePasswordButton.setOnClickListener {
            if (passwordInput.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.baseline_lock_open_24)
            } else {
                // Hide password
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordButton.setImageResource(R.drawable.baseline_lock_24)
            }
            passwordInput.setSelection(passwordInput.text?.length ?: 0)
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
            // TODO: Implement forgot password flow
        }

        signInButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish() // close LoginActivity
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUp_Activity::class.java))
        }
    }
}
