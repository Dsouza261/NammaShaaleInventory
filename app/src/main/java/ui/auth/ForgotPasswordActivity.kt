package com.nammashaalee.inventory.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nammashaalee.inventory.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackForgot.setOnClickListener { finish() }
        binding.btnBackToLogin.setOnClickListener { finish() }

        binding.btnSendReset.setOnClickListener {
            val email = binding.etForgotEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.etForgotEmail.error = "Please enter your email"
                return@setOnClickListener
            }

            binding.btnSendReset.isEnabled = false
            binding.btnSendReset.text = "Sending..."

            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    binding.tvForgotSuccess.visibility = View.VISIBLE
                    binding.btnSendReset.text = "Sent!"
                }
                .addOnFailureListener { e ->
                    binding.btnSendReset.isEnabled = true
                    binding.btnSendReset.text = "Send Reset Link"
                    binding.etForgotEmail.error = e.message
                }
        }
    }
}