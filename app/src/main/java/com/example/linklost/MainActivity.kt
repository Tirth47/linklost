package com.example.linklost

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            loginUser()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser() {

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty()) {
            etEmail.error = "Enter email"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Enter password"
            etPassword.requestFocus()
            return
        }

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        /*
         * Create the same unique key used during registration.
         *
         * Example:
         * tirth@gnu.ac.in
         * becomes:
         * user_tirth_gnu_ac_in
         */

        val safeEmail = email
            .lowercase()
            .replace("@", "_")
            .replace(".", "_")

        val userPrefix = "user_$safeEmail"

        val savedEmail = preferences.getString(
            "${userPrefix}_email",
            null
        )

        val savedPassword = preferences.getString(
            "${userPrefix}_password",
            null
        )

        if (
            savedEmail != null &&
            savedPassword != null &&
            email.equals(savedEmail, ignoreCase = true) &&
            password == savedPassword
        ) {

            // Store the currently logged-in account
            preferences.edit()
                .putString("loggedInEmail", savedEmail)
                .apply()

            Toast.makeText(
                this,
                "Login successful!",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                HomeActivity::class.java
            )

            startActivity(intent)
            finish()

        } else {

            Toast.makeText(
                this,
                "Invalid email or password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}