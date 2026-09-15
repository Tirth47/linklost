package com.example.linklost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText

class BrowseActivity : AppCompatActivity() {

    private lateinit var recyclerItems: RecyclerView
    private lateinit var tvEmptyItems: TextView
    private lateinit var etBrowseSearch: TextInputEditText
    private lateinit var filterToggleGroup: MaterialButtonToggleGroup

    private lateinit var adapter: ReportAdapter

    private var allReports = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        recyclerItems = findViewById(R.id.recyclerItems)
        tvEmptyItems = findViewById(R.id.tvEmptyItems)
        etBrowseSearch = findViewById(R.id.etBrowseSearch)
        filterToggleGroup = findViewById(R.id.filterToggleGroup)

        val btnFilterAll = findViewById<MaterialButton>(R.id.btnFilterAll)
        val btnFilterLost = findViewById<MaterialButton>(R.id.btnFilterLost)
        val btnFilterFound = findViewById<MaterialButton>(R.id.btnFilterFound)

        recyclerItems.layoutManager = LinearLayoutManager(this)

        adapter = ReportAdapter(emptyList())
        recyclerItems.adapter = adapter

        loadReports()
        val homeSearchText = intent.getStringExtra("searchText")

        if (!homeSearchText.isNullOrEmpty()) {
            etBrowseSearch.setText(homeSearchText)
        }

        etBrowseSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        btnFilterAll.setOnClickListener {
            applyFilters()
        }

        btnFilterLost.setOnClickListener {
            applyFilters()
        }

        btnFilterFound.setOnClickListener {
            applyFilters()
        }
    }

    private fun loadReports() {

        val preferences = getSharedPreferences(
            "LostLinkData",
            MODE_PRIVATE
        )

        val savedReports = preferences.getString("reports", "")

        allReports = if (savedReports.isNullOrEmpty()) {
            emptyList()
        } else {
            savedReports.split("\n")
        }

        applyFilters()
    }

    private fun applyFilters() {

        val searchText = etBrowseSearch.text.toString()
            .trim()
            .lowercase()

        val selectedButton = filterToggleGroup.checkedButtonId

        val filteredReports = allReports.filter { report ->

            val parts = report.split("|")

            if (parts.size >= 7) {

                // New format:
                // Email | Type | Item | Category | Location | Description | Date

                val type = parts[1]
                val itemName = parts[2]
                val category = parts[3]
                val location = parts[4]
                val description = parts[5]
                val dateTime = parts[6]

                val matchesType = when (selectedButton) {

                    R.id.btnFilterLost -> type == "LOST"

                    R.id.btnFilterFound -> type == "FOUND"

                    else -> true
                }

                val matchesSearch =
                    searchText.isEmpty() ||
                            itemName.lowercase().contains(searchText) ||
                            category.lowercase().contains(searchText) ||
                            location.lowercase().contains(searchText) ||
                            description.lowercase().contains(searchText) ||
                            dateTime.lowercase().contains(searchText)

                matchesType && matchesSearch

            } else if (parts.size >= 5) {

                // Old report format

                val type = parts[0]
                val itemName = parts[1]
                val category = parts[2]
                val location = parts[3]
                val description = parts[4]

                val matchesType = when (selectedButton) {

                    R.id.btnFilterLost -> type == "LOST"

                    R.id.btnFilterFound -> type == "FOUND"

                    else -> true
                }

                val matchesSearch =
                    searchText.isEmpty() ||
                            itemName.lowercase().contains(searchText) ||
                            category.lowercase().contains(searchText) ||
                            location.lowercase().contains(searchText) ||
                            description.lowercase().contains(searchText)

                matchesType && matchesSearch

            } else {
                false
            }
        }

        adapter.updateReports(filteredReports)

        if (filteredReports.isEmpty()) {

            recyclerItems.visibility = RecyclerView.GONE
            tvEmptyItems.visibility = TextView.VISIBLE

        } else {

            recyclerItems.visibility = RecyclerView.VISIBLE
            tvEmptyItems.visibility = TextView.GONE
        }
    }
}