package com.example.linklost

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        // Get currently logged-in email
        val loggedInEmail = preferences.getString(
            "loggedInEmail",
            ""
        ) ?: ""

        // Create the same key used during registration
        val safeEmail = loggedInEmail
            .lowercase()
            .replace("@", "_")
            .replace(".", "_")

        val userPrefix = "user_$safeEmail"

        // Get the name of the currently logged-in user
        val name = preferences.getString(
            "${userPrefix}_name",
            "Student"
        ) ?: "Student"

        tvWelcome.text = "Welcome, $name 👋"

        val etSearch = findViewById<EditText>(R.id.etSearch)

        val btnBrowse = findViewById<MaterialButton>(R.id.btnBrowse)
        val btnReportLost = findViewById<MaterialButton>(R.id.btnReportLost)
        val btnReportFound = findViewById<MaterialButton>(R.id.btnReportFound)
        val btnMyReports = findViewById<MaterialButton>(R.id.btnMyReports)
        val btnProfile = findViewById<MaterialButton>(R.id.btnProfile)

        btnBrowse.setOnClickListener {

            val intent = Intent(
                this,
                BrowseActivity::class.java
            )

            val searchText = etSearch.text.toString().trim()

            intent.putExtra(
                "searchText",
                searchText
            )

            startActivity(intent)
        }

        btnReportLost.setOnClickListener {

            val intent = Intent(
                this,
                ReportActivity::class.java
            )

            intent.putExtra(
                "reportType",
                "LOST"
            )

            startActivity(intent)
        }

        btnReportFound.setOnClickListener {

            val intent = Intent(
                this,
                ReportActivity::class.java
            )

            intent.putExtra(
                "reportType",
                "FOUND"
            )

            startActivity(intent)
        }

        btnMyReports.setOnClickListener {

            val intent = Intent(
                this,
                MyReportsActivity::class.java
            )

            startActivity(intent)
        }

        btnProfile.setOnClickListener {

            val intent = Intent(
                this,
                ProfileActivity::class.java
            )

            startActivity(intent)
        }
    }
}