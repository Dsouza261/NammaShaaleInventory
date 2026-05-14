package com.nammashaalee.inventory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.nammashaalee.inventory.MainActivity
import com.nammashaalee.inventory.databinding.ActivityOtpBinding
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private val auth = FirebaseAuth.getInstance()
    private var verificationId: String = ""
    private var phone: String = ""
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phone = intent.getStringExtra("phone") ?: ""
        binding.tvOtpPhone.text = "OTP sent to $phone"

        binding.btnBackOtp.setOnClickListener { finish() }

        setupOtpBoxes()
        sendOtp()
        startResendTimer()

        binding.btnVerifyOtp.setOnClickListener { verifyOtp() }
        binding.btnResendOtp.setOnClickListener {
            sendOtp()
            startResendTimer()
        }
    }

    private fun sendOtp() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential)
                }
                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    binding.tvErrorOtp.text = e.message
                    binding.tvErrorOtp.visibility = View.VISIBLE
                }
                override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    verificationId = vId
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp() {
        val otp = binding.otp1.text.toString() +
                binding.otp2.text.toString() +
                binding.otp3.text.toString() +
                binding.otp4.text.toString()

        if (otp.length < 4) {
            binding.tvErrorOtp.text = "Please enter the 4-digit OTP"
            binding.tvErrorOtp.visibility = View.VISIBLE
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {
                binding.tvErrorOtp.text = "Invalid OTP. Please try again."
                binding.tvErrorOtp.visibility = View.VISIBLE
            }
    }

    private fun startResendTimer() {
        binding.btnResendOtp.visibility = View.GONE
        binding.tvResendTimer.visibility = View.VISIBLE
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.tvResendTimer.text = "Resend code in 0:${seconds.toString().padStart(2, '0')}"
            }
            override fun onFinish() {
                binding.tvResendTimer.visibility = View.GONE
                binding.btnResendOtp.visibility = View.VISIBLE
            }
        }.start()
    }

    // Auto-focus next OTP box on input
    private fun setupOtpBoxes() {
        val boxes = listOf(binding.otp1, binding.otp2, binding.otp3, binding.otp4)
        boxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < boxes.size - 1) {
                        boxes[index + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}