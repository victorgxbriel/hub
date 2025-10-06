package com.example.timer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timer.databinding.ListItemLapBinding
import com.example.timer.ui.model.LapUiModel

class LapAdapter : ListAdapter<LapUiModel, LapAdapter.LapViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        val binding = ListItemLapBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LapViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        val currentLap = getItem(position)
        holder.bind(currentLap)
    }

    class LapViewHolder(private val binding: ListItemLapBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lap: LapUiModel) {
            binding.lapNumberText.text = "Volta ${lap.number}"
            binding.lapTimeText.text = lap.formattedTime
            binding.lapTimeText.setTextColor(lap.textColor)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LapUiModel>() {
            override fun areItemsTheSame(oldItem: LapUiModel, newItem: LapUiModel) =
                oldItem.number == newItem.number

            override fun areContentsTheSame(oldItem: LapUiModel, newItem: LapUiModel) =
                oldItem == newItem
        }
    }
}