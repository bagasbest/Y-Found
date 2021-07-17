package com.yfound.yfound.ui.pengiriman_barang

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yfound.yfound.databinding.ItemDeliveryBinding

class DeliveryAdapter : RecyclerView.Adapter<DeliveryAdapter.ViewHolder>() {

    private val listDelivery = ArrayList<DeliveryModel>()
    fun setData(items: ArrayList<DeliveryModel>) {
        listDelivery.clear()
        listDelivery.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDeliveryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listDelivery[position])
    }

    override fun getItemCount(): Int = listDelivery.size

    inner class ViewHolder(private val binding: ItemDeliveryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: DeliveryModel) {
            with(binding) {
                date.text = "Waktu pengiriman: " + model.deliveryDate
                location.text = "Tujuan pengiriman: " + model.location

                view.setOnClickListener {
                    val intent = Intent(itemView.context, DeliveryDetailActivity::class.java)
                    intent.putExtra(DeliveryDetailActivity.EXTRA_DELIVERY, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }


}