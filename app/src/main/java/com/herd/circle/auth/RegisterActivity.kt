package com.herd.circle.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.herd.circle.MainActivity
import com.herd.circle.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

/**
 * Register screen — see Part 1 Design Document, Section 4.2.
 * Creates the account with Firebase Auth. The user's profile document
 * (fullName, username, etc.) would be written to Firestore via the REST
 * layer immediately after — left as a follow-up call in a full build.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()

    companion object {
        private const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener { attemptRegister() }
    }

    private fun attemptRegister() {
        val fullName = binding.etFullName.text?.toString()?.trim().orEmpty()
        val username = binding.etUsername.text?.toString()?.trim().orEmpty()
        val email = binding.etEmail.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Please fill all fields (password 6+ chars)", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            val result = authRepository.register(fullName, email, password)
            setLoading(false)
            result.onSuccess { uid ->
                Log.i(TAG, "Registered new user $uid ($username)")
                Toast.makeText(this@RegisterActivity, "Account created!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()
            }.onFailure { error ->
                Log.e(TAG, "Registration failed", error)
                Toast.makeText(this@RegisterActivity, "Sign up failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !loading
    }
}
