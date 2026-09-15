package com.example.linklost

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyReportsActivity : AppCompatActivity() {

    private lateinit var recyclerMyReports: RecyclerView
    private lateinit var tvNoReports: TextView

    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reports)

        recyclerMyReports = findViewById(R.id.recyclerMyReports)
        tvNoReports = findViewById(R.id.tvNoReports)

        recyclerMyReports.layoutManager = LinearLayoutManager(this)

        // Delete + Close callbacks
        adapter = ReportAdapter(
            emptyList(),
            onDeleteClick = { report ->
                showDeleteConfirmation(report)
            },
            onCloseClick = { report ->
                showCloseConfirmation(report)
            }
        )

        recyclerMyReports.adapter = adapter

        loadMyReports()
    }

    override fun onResume() {
        super.onResume()
        loadMyReports()
    }

    private fun loadMyReports() {

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        val loggedInEmail = preferences.getString(
            "loggedInEmail",
            ""
        ) ?: ""

        val savedReports = preferences.getString(
            "reports",
            ""
        )

        val allReports = if (savedReports.isNullOrEmpty()) {
            emptyList()
        } else {
            savedReports.split("\n")
        }

        val myReports = allReports.filter { report ->

            val parts = report.split("|")

            if (parts.size >= 7) {

                // Current format:
                // Email | Type | Item | Category | Location | Description | Date | Status

                parts[0].equals(
                    loggedInEmail,
                    ignoreCase = true
                )

            } else {
                false
            }
        }

        adapter.updateReports(myReports)

        if (myReports.isEmpty()) {

            recyclerMyReports.visibility = RecyclerView.GONE
            tvNoReports.visibility = TextView.VISIBLE

        } else {

            recyclerMyReports.visibility = RecyclerView.VISIBLE
            tvNoReports.visibility = TextView.GONE
        }
    }

    private fun deleteReport(reportToDelete: String) {

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        val savedReports = preferences.getString(
            "reports",
            ""
        )

        if (savedReports.isNullOrEmpty()) {
            return
        }

        val allReports = savedReports
            .split("\n")
            .toMutableList()

        allReports.remove(reportToDelete)

        val updatedReports = allReports.joinToString("\n")

        preferences.edit()
            .putString("reports", updatedReports)
            .apply()

        loadMyReports()
    }

    private fun closeReport(reportToClose: String) {

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        val savedReports = preferences.getString(
            "reports",
            ""
        )

        if (savedReports.isNullOrEmpty()) {
            return
        }

        val allReports = savedReports
            .split("\n")
            .toMutableList()

        val index = allReports.indexOf(reportToClose)

        if (index == -1) {
            return
        }

        val parts = reportToClose.split("|")

        if (parts.size >= 8) {

            // Change only the status:
            // Email | Type | Item | Category | Location | Description | Date | CLOSED

            parts.toMutableList().also {
                it[7] = "CLOSED"
                allReports[index] = it.joinToString("|")
            }

        } else if (parts.size >= 7) {

            // Convert old report format to the new format.
            allReports[index] = reportToClose + "|CLOSED"

        } else {
            return
        }

        val updatedReports = allReports.joinToString("\n")

        preferences.edit()
            .putString("reports", updatedReports)
            .apply()

        loadMyReports()
    }

    private fun showDeleteConfirmation(report: String) {

        val parts = report.split("|")

        val itemName = if (parts.size >= 3) {
            parts[2]
        } else {
            "this report"
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Report?")
            .setMessage(
                "Are you sure you want to delete the report for \"$itemName\"?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                deleteReport(report)
            }
            .show()
    }

    private fun showCloseConfirmation(report: String) {

        val parts = report.split("|")

        val itemName = if (parts.size >= 3) {
            parts[2]
        } else {
            "this report"
        }

        AlertDialog.Builder(this)
            .setTitle("Close Report?")
            .setMessage(
                "Mark \"$itemName\" as closed?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Close") { _, _ ->
                closeReport(report)
            }
            .show()
    }
}