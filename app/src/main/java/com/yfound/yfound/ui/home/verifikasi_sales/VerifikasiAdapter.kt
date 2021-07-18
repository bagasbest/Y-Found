package com.yfound.yfound.ui.home.verifikasi_sales

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yfound.yfound.databinding.ItemVerificationBinding

class VerifikasiAdapter(private val status: String) : RecyclerView.Adapter<VerifikasiAdapter.ViewHolder>() {

    private val listSales = ArrayList<VerifikasiModel>()
    fun setData(items: ArrayList<VerifikasiModel>) {
        listSales.clear()
        listSales.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSales[position])
    }

    override fun getItemCount(): Int = listSales.size

    inner class ViewHolder (private val binding: ItemVerificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: VerifikasiModel) {
            with(binding) {
                name.text = model.name
                view.setOnClickListener{
                    val intent =  Intent(itemView.context, VerifikasiDetailActivity::class.java)
                    intent.putExtra(VerifikasiDetailActivity.EXTRA_SALES, model)
                    intent.putExtra(VerifikasiDetailActivity.EXTRA_STATUS, status)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }
}