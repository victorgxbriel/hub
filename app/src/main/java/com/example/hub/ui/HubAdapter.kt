package com.example.hub.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hub.databinding.GridItemAppBinding

class HubAdapter(
    private val onItemClicked: (AppItem) -> Unit
) : ListAdapter<AppItem, HubAdapter.AppItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppItemViewHolder {
        val binding = GridItemAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(currentItem)
        }
        holder.bind(currentItem)
    }

    class AppItemViewHolder(private val binding: GridItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AppItem) {
            binding.itemName.text = item.name
            binding.itemIcon.setImageResource(item.iconRes)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppItem>() {
            override fun areItemsTheSame(oldItem: AppItem, newItem: AppItem) =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: AppItem, newItem: AppItem) =
                oldItem == newItem
        }
    }
}