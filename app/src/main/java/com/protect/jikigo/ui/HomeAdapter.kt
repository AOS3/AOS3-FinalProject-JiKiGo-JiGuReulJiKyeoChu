package com.protect.jikigo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.protect.jikigo.data.Coupon
import com.protect.jikigo.databinding.ItemCouponBinding
import com.protect.jikigo.ui.extensions.applyNumberFormat

class HomeAdapter(
    private val items: List<Coupon>,
    private val listener: HomeStoreItemClickListener
) : RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder.from(parent, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(items[position])
    }
}


class HomeViewHolder(
    private val binding: ItemCouponBinding,
    private val listener: HomeStoreItemClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Coupon) {
        itemView.setOnClickListener {
            listener.onClickStore(item)
        }
        with(binding) {
            Glide.with(root.context)
                .load(item.image) // 이미지 URL
                .into(ivCouponDetailThumnail); // 이미지가 로드될 ImageView

            tvCouponName.text = item.name
            tvCouponPrice.applyNumberFormat(item.price)
            tvCouponBrand.text = item.brand
        }

    }

    companion object {
        fun from(parent: ViewGroup, listener: HomeStoreItemClickListener): HomeViewHolder {
            return HomeViewHolder(
                ItemCouponBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), listener
            )
        }
    }
}