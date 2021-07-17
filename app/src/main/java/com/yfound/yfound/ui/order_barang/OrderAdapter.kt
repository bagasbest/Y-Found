package com.yfound.yfound.ui.order_barang

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yfound.yfound.databinding.ItemOrderBinding

class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val listOrder = ArrayList<OrderModel>()
    fun setData(items: ArrayList<OrderModel>) {
        listOrder.clear()
        listOrder.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOrder[position])
    }

    override fun getItemCount(): Int = listOrder.size

    inner class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: OrderModel) {
            with(binding) {
                buyerName.text = "Sales: " + model.buyerName
                orderDate.text = "Tanggal Order: " + model.orderDate
                location.text = "Tujuan Pengiriman: " + model.location

                var product: String? = ""
                for (i in model.cart!!.indices) {
                    if(i < model.cart!!.size-1) {
                        product = product + model.cart!![i].name + ", "
                    }else {
                        product += model.cart!![i].name
                    }
                }
                cart.text = "Barang: $product"

                view3.setOnClickListener {
                    val intent = Intent(itemView.context, OrderDetailActivity::class.java)
                    intent.putExtra(OrderDetailActivity.EXTRA_ORDER, model)
                    intent.putExtra(OrderDetailActivity.EXTRA_STATUS, model.status)
                    itemView.context.startActivity(intent)
                }

            }
        }
    }
}