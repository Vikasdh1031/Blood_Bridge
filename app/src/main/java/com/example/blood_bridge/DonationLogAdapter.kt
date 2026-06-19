package com.example.bloodbridge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbridge.databinding.ItemDonationLogBinding
import java.text.SimpleDateFormat
import java.util.*

class DonationLogAdapter(private var logs: List<DonationLog>) :
    RecyclerView.Adapter<DonationLogAdapter.LogViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class LogViewHolder(val binding: ItemDonationLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemDonationLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        holder.binding.tvLogBloodGroup.text = log.bloodGroup
        holder.binding.tvLogDonorName.text = log.donorName
        holder.binding.tvLogLocation.text = "📍 ${log.location}"
        holder.binding.tvLogDate.text = "🗓 ${dateFormat.format(Date(log.donationDate))}"
    }

    override fun getItemCount() = logs.size

    fun updateList(newList: List<DonationLog>) {
        logs = newList
        notifyDataSetChanged()
    }
}