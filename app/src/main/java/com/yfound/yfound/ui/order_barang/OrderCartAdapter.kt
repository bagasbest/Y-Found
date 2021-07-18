package com.yfound.yfound.ui.order_barang

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yfound.yfound.databinding.ItemCartBinding

class OrderCartAdapter(
    private val role: String,
    private val sendQTYValue: ArrayList<OrderQtyModel>,
    private val status: String?,
    private var visibility: Button?
) : RecyclerView.Adapter<OrderCartAdapter.ViewHolder>() {

    private val listOrderCart = ArrayList<OrderCartModel2>()
    fun setData(items: ArrayList<OrderCartModel2>) {
        listOrderCart.clear()
        listOrderCart.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOrderCart[position])
    }

    override fun getItemCount(): Int = listOrderCart.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: OrderCartModel2) {
            with(binding) {

                delete.visibility = View.GONE

                Glide.with(itemView.context)
                    .load(model.dp)
                    .into(dp)

                if(status == "shipped") {
                    sendQtyView.isEnabled = false
                }

                if (role == "admin") {
                    sendQtyView.visibility = View.VISIBLE
                    sendQty.setText(model.quantity)
                    sendQty.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }

                        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                            if(p0.isNotEmpty() && p0.toString().toInt() <= model.quantity.toString().toInt()) {
                                visibility?.visibility = View.VISIBLE
                                val qtyHold = Integer.parseInt(model.quantity.toString()) - Integer.parseInt(p0.toString())
                                sendQTYValue[layoutPosition] = OrderQtyModel(p0.toString(), qtyHold.toString())
                            }
                            else {
                                visibility?.visibility = View.GONE
                                Toast.makeText(itemView.context, "Ups, minimal 0 barang\nmaksimal ${model.quantity} barang", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun afterTextChanged(p0: Editable?) {
                        }
                    })
                } else {
                    sendQtyView.visibility = View.GONE
                }


                Glide.with(itemView.context)
                    .load(model.dp)
                    .into(dp)

                name.text = model.name
                quantiy.text = "Kuantitas: " + model.quantity

            }
        }
    }
}
