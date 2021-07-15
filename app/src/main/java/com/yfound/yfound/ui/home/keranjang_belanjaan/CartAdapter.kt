package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yfound.yfound.databinding.ItemCartBinding

class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private val listCart = ArrayList<CartModel>()
    fun setData(items: ArrayList<CartModel>) {
        listCart.clear()
        listCart.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listCart[position])
    }

    override fun getItemCount(): Int = listCart.size

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartModel: CartModel) {
            with(binding) {
                Glide
                    .with(itemView.context)
                    .load(cartModel.dp)
                    .into(dp)

                name.text = cartModel.name
                quantiy.text = "Kuantitas: " + cartModel.quantity

                delete.setOnClickListener {
                    listCart.remove(listCart[adapterPosition])
                    notifyDataSetChanged()
                }
            }
        }
    }
}