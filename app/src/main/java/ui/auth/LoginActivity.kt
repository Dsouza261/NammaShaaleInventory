package com.nammashaalee.inventory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nammashaalee.inventory.MainActivity
import com.nammashaalee.inventory.R
import com.nammashaalee.inventory.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth = FirebaseAuth.getInstance()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            binding.btnGoogleSignin.isEnabled = true
            showError("Google Sign-In failed: ${e.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener { doLogin() }

        binding.btnGoogleSignin.setOnClickListener {
            binding.btnGoogleSignin.isEnabled = false
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        // Replace OTP button with second Google sign in hint
        binding.btnPhoneOtp.text = "Sign in with Google (School Account)"
        binding.btnPhoneOtp.setOnClickListener {
            binding.btnPhoneOtp.isEnabled = false
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.tvGoSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                goToDashboard()
            }
            .addOnFailureListener { e ->
                binding.btnGoogleSignin.isEnabled = true
                binding.btnPhoneOtp.isEnabled = true
                showError("Authentication failed: ${e.message}")
            }
    }

    private fun doLogin() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter email and password")
            return
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Signing in..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                goToDashboard()
            }
            .addOnFailureListener { e ->
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Sign In"
                showError(e.message ?: "Login failed. Please try again.")
            }
    }

    private fun showError(msg: String) {
        binding.tvLoginError.text = msg
        binding.tvLoginError.visibility = View.VISIBLE
    }

    private fun goToDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}