package com.example.linklost

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText

class ReportActivity : AppCompatActivity() {

    private lateinit var etItemName: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etDescription: TextInputEditText

    private lateinit var typeToggleGroup: MaterialButtonToggleGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        etItemName = findViewById(R.id.etItemName)
        etCategory = findViewById(R.id.etCategory)
        etLocation = findViewById(R.id.etLocation)
        etDescription = findViewById(R.id.etDescription)

        typeToggleGroup = findViewById(R.id.typeToggleGroup)

        val btnSaveReport = findViewById<MaterialButton>(R.id.btnSaveReport)

        val reportType = intent.getStringExtra("reportType")

        if (reportType == "LOST") {
            typeToggleGroup.check(R.id.btnTypeLost)
        } else if (reportType == "FOUND") {
            typeToggleGroup.check(R.id.btnTypeFound)
        }

        btnSaveReport.setOnClickListener {
            saveReport()
        }
    }

    private fun saveReport() {

        val itemName = etItemName.text.toString().trim()
        val category = etCategory.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val description = etDescription.text.toString().trim()

        val selectedButton = typeToggleGroup.checkedButtonId

        if (selectedButton == -1) {
            Toast.makeText(
                this,
                "Select Lost or Found",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val type = if (selectedButton == R.id.btnTypeLost) {
            "LOST"
        } else {
            "FOUND"
        }

        if (itemName.isEmpty()) {
            etItemName.error = "Enter item name"
            etItemName.requestFocus()
            return
        }

        if (category.isEmpty()) {
            etCategory.error = "Enter category"
            etCategory.requestFocus()
            return
        }

        if (location.isEmpty()) {
            etLocation.error = "Enter location"
            etLocation.requestFocus()
            return
        }

        if (description.isEmpty()) {
            etDescription.error = "Enter description"
            etDescription.requestFocus()
            return
        }

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        // Get currently logged-in account
        val loggedInEmail = preferences.getString(
            "loggedInEmail",
            ""
        )?.trim()?.lowercase() ?: ""

        // Make sure a user is actually logged in
        if (loggedInEmail.isEmpty()) {
            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val oldReports = preferences.getString(
            "reports",
            ""
        )

        val dateTime = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val status = "ACTIVE"

        // Format:
        // Email | Type | Item | Category | Location | Description | DateTime | Status
        val newReport =
            "$loggedInEmail|$type|$itemName|$category|$location|$description|$dateTime|$status"

        val updatedReports = if (oldReports.isNullOrEmpty()) {
            newReport
        } else {
            "$oldReports\n$newReport"
        }

        preferences.edit()
            .putString("reports", updatedReports)
            .apply()

        Toast.makeText(
            this,
            "$type item reported successfully!",
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }
}