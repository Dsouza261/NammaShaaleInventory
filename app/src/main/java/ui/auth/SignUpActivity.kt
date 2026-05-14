package com.nammashaalee.inventory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.nammashaalee.inventory.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val auth = FirebaseAuth.getInstance()
    private val roles = listOf("Teacher", "Principal", "Admin", "Staff")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.btnBackSignup.setOnClickListener { finish() }
        binding.tvGoLogin.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener { doSignUp() }
    }

    private fun doSignUp() {
        val name = binding.etSignupName.text.toString().trim()
        val school = binding.etSignupSchool.text.toString().trim()
        val email = binding.etSignupEmail.text.toString().trim()
        val password = binding.etSignupPassword.text.toString().trim()
        val role = roles[binding.spinnerRole.selectedItemPosition]

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill all required fields")
            return
        }
        if (password.length < 8) {
            showError("Password must be at least 8 characters")
            return
        }

        binding.btnCreateAccount.isEnabled = false
        binding.btnCreateAccount.text = "Creating account..."

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener

                // Update display name
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName("$name · $school · $role")
                    .build()
                user.updateProfile(profileUpdate)

                // Send email verification
                user.sendEmailVerification()
                    .addOnSuccessListener {
                        // Sign out until email is verified
                        auth.signOut()
                        binding.btnCreateAccount.isEnabled = true
                        binding.btnCreateAccount.text = "Create Account"
                        showSuccess("✓ Account created! Check your email to verify before signing in.")
                    }
                    .addOnFailureListener {
                        // Even if verification email fails, sign out
                        auth.signOut()
                        binding.btnCreateAccount.isEnabled = true
                        binding.btnCreateAccount.text = "Create Account"
                        showSuccess("✓ Account created! Please verify your email then sign in.")
                    }
            }
            .addOnFailureListener { e ->
                binding.btnCreateAccount.isEnabled = true
                binding.btnCreateAccount.text = "Create Account"
                showError(e.message ?: "Sign up failed. Please try again.")
            }
    }

    private fun showError(msg: String) {
        binding.tvSignupError.text = msg
        binding.tvSignupError.setBackgroundResource(com.nammashaalee.inventory.R.color.red_light)
        binding.tvSignupError.visibility = View.VISIBLE
    }

    private fun showSuccess(msg: String) {
        binding.tvSignupError.text = msg
        binding.tvSignupError.setBackgroundResource(com.nammashaalee.inventory.R.color.green_light)
        binding.tvSignupError.visibility = View.VISIBLE
    }
}