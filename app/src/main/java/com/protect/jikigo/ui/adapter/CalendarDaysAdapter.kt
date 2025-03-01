package com.protect.jikigo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.protect.jikigo.databinding.ItemCalendarDaysListBinding

class CalendarDaysAdapter : RecyclerView.Adapter<CalendarDaysAdapter.CalendarDaysViewHolder>() {
    private val items = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDaysViewHolder {
        return CalendarDaysViewHolder(
            ItemCalendarDaysListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CalendarDaysViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun submitList(item: List<Int>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    class CalendarDaysViewHolder(private val binding: ItemCalendarDaysListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {
            if (item != 0) {
                binding.textView3.visibility = View.VISIBLE
            }
            binding.textView3.text = item.toString()
        }
    }
}