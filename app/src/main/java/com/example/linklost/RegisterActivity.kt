package com.example.linklost

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etStudentId: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etStudentId = findViewById(R.id.etStudentId)
        etPassword = findViewById(R.id.etPassword)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val studentId = etStudentId.text.toString().trim()
        val password = etPassword.text.toString()

        if (name.isEmpty()) {
            etName.error = "Enter your name"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Enter your college email"
            etEmail.requestFocus()
            return
        }

        if (studentId.isEmpty()) {
            etStudentId.error = "Enter student ID"
            etStudentId.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        /*
         * Create a unique key for this email.
         *
         * Example:
         * user_tirth_gnu_ac_in_name
         * user_tirth_gnu_ac_in_password
         *
         * This prevents one account from overwriting another.
         */

        val safeEmail = email
            .lowercase()
            .replace("@", "_")
            .replace(".", "_")

        val userPrefix = "user_$safeEmail"

        val existingEmail = preferences.getString(
            "${userPrefix}_email",
            null
        )

        if (existingEmail != null) {
            Toast.makeText(
                this,
                "This email is already registered",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        preferences.edit()
            .putString("${userPrefix}_name", name)
            .putString("${userPrefix}_email", email)
            .putString("${userPrefix}_studentId", studentId)
            .putString("${userPrefix}_password", password)
            .apply()

        Toast.makeText(
            this,
            "Registration successful!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}