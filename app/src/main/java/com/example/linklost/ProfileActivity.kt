package com.example.linklost

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvStudentId = findViewById<TextView>(R.id.tvProfileStudentId)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        // Get currently logged-in user's email
        val loggedInEmail = preferences.getString(
            "loggedInEmail",
            ""
        ) ?: ""

        // Create the same account key used during registration
        val safeEmail = loggedInEmail
            .lowercase()
            .replace("@", "_")
            .replace(".", "_")

        val userPrefix = "user_$safeEmail"

        // Get current user's details
        val name = preferences.getString(
            "${userPrefix}_name",
            "Student"
        ) ?: "Student"

        val email = preferences.getString(
            "${userPrefix}_email",
            loggedInEmail
        ) ?: loggedInEmail

        val studentId = preferences.getString(
            "${userPrefix}_studentId",
            "Not available"
        ) ?: "Not available"

        // Display current user's details
        tvName.text = name
        tvEmail.text = email
        tvStudentId.text = studentId

        // Logout
        btnLogout.setOnClickListener {

            preferences.edit()
                .remove("loggedInEmail")
                .apply()

            val intent = Intent(this, MainActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }
    }
}