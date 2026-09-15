package com.example.linklost

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class ReportAdapter(
    private var reports: List<String>,
    private val onDeleteClick: ((String) -> Unit)? = null,
    private val onCloseClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvReportType: TextView =
            itemView.findViewById(R.id.tvReportType)

        val tvReportStatus: TextView =
            itemView.findViewById(R.id.tvReportStatus)

        val tvItemName: TextView =
            itemView.findViewById(R.id.tvItemName)

        val tvCategory: TextView =
            itemView.findViewById(R.id.tvCategory)

        val tvLocation: TextView =
            itemView.findViewById(R.id.tvLocation)

        val tvDescription: TextView =
            itemView.findViewById(R.id.tvDescription)

        val tvReportDate: TextView =
            itemView.findViewById(R.id.tvReportDate)

        val btnDeleteReport: MaterialButton =
            itemView.findViewById(R.id.btnDeleteReport)

        val btnCloseReport: MaterialButton =
            itemView.findViewById(R.id.btnCloseReport)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReportViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ReportViewHolder,
        position: Int
    ) {

        val report = reports[position]
        val parts = report.split("|")

        var status = "ACTIVE"

        if (parts.size >= 8) {

            // Current format:
            // Email | Type | Item | Category | Location | Description | Date/Time | Status

            val type = parts[1]
            val itemName = parts[2]
            val category = parts[3]
            val location = parts[4]
            val description = parts[5]
            val dateTime = parts[6]

            status = parts[7]

            holder.tvReportType.text = type
            holder.tvReportStatus.text = status

            holder.tvItemName.text = itemName
            holder.tvCategory.text = "Category: $category"
            holder.tvLocation.text = "Location: $location"
            holder.tvDescription.text = description
            holder.tvReportDate.text = "Reported: $dateTime"

        } else if (parts.size >= 7) {

            // Previous format:
            // Email | Type | Item | Category | Location | Description | Date/Time

            val type = parts[1]
            val itemName = parts[2]
            val category = parts[3]
            val location = parts[4]
            val description = parts[5]
            val dateTime = parts[6]

            status = "ACTIVE"

            holder.tvReportType.text = type
            holder.tvReportStatus.text = status

            holder.tvItemName.text = itemName
            holder.tvCategory.text = "Category: $category"
            holder.tvLocation.text = "Location: $location"
            holder.tvDescription.text = description
            holder.tvReportDate.text = "Reported: $dateTime"

        } else if (parts.size >= 5) {

            // Old reports created before the email update

            val type = parts[0]
            val itemName = parts[1]
            val category = parts[2]
            val location = parts[3]
            val description = parts[4]

            status = "ACTIVE"

            holder.tvReportType.text = type
            holder.tvReportStatus.text = status

            holder.tvItemName.text = itemName
            holder.tvCategory.text = "Category: $category"
            holder.tvLocation.text = "Location: $location"
            holder.tvDescription.text = description
            holder.tvReportDate.text = ""
        }

        // Status color
        setStatusColor(
            holder.tvReportStatus,
            status
        )

        // DELETE BUTTON
        if (onDeleteClick != null) {

            holder.btnDeleteReport.visibility = View.VISIBLE

            holder.btnDeleteReport.setOnClickListener {
                onDeleteClick.invoke(report)
            }

        } else {

            // Hidden in Browse screen
            holder.btnDeleteReport.visibility = View.GONE
            holder.btnDeleteReport.setOnClickListener(null)
        }

        // CLOSE BUTTON
        if (onCloseClick != null &&
            !status.equals("CLOSED", ignoreCase = true)
        ) {

            // Show only for ACTIVE reports
            holder.btnCloseReport.visibility = View.VISIBLE

            holder.btnCloseReport.setOnClickListener {
                onCloseClick.invoke(report)
            }

        } else {

            // Hide when:
            // 1. Report is already CLOSED
            // 2. Browse screen is using the adapter
            holder.btnCloseReport.visibility = View.GONE
            holder.btnCloseReport.setOnClickListener(null)
        }
    }

    private fun setStatusColor(
        statusView: TextView,
        status: String
    ) {

        if (status.equals("CLOSED", ignoreCase = true)) {

            // CLOSED = Red
            statusView.setTextColor(
                Color.rgb(198, 40, 40)
            )

        } else {

            // ACTIVE = Green
            statusView.setTextColor(
                Color.rgb(46, 125, 50)
            )
        }
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    fun updateReports(newReports: List<String>) {
        reports = newReports
        notifyDataSetChanged()
    }
}