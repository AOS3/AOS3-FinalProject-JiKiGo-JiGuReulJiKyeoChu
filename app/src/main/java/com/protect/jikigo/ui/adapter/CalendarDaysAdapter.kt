package com.protect.jikigo.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.protect.jikigo.data.model.UserPaymentHistory
import com.protect.jikigo.databinding.ItemCalendarDaysListBinding

class CalendarDaysAdapter : RecyclerView.Adapter<CalendarDaysAdapter.CalendarDaysViewHolder>() {
    private val items = mutableListOf<Int>()
    private val pointItems = mutableListOf<UserPaymentHistory>()

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
        holder.bind(items[position], pointItems)
    }

    fun submitList(item: List<Int>) {
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    fun submitPointList(item: List<UserPaymentHistory>) {
        pointItems.clear()
        pointItems.addAll(item)
        notifyDataSetChanged()
    }

    class CalendarDaysViewHolder(private val binding: ItemCalendarDaysListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int, pointItems: List<UserPaymentHistory>) {
            if (item != 0) {
                binding.textView3.visibility = View.VISIBLE
            }
            val date = String.format("%02d", item)
            pointItems.forEach {
                Log.d("test", it.paymentDate.substring(9, 11))
                if (it.paymentDate.substring(9, 11) == date) {
                binding.textView4.text = it.amount.toString()
            } }
            binding.textView3.text = item.toString()
        }
    }
}