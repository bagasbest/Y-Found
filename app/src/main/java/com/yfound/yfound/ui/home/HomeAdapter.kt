package com.yfound.yfound.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yfound.yfound.databinding.ItemProductBinding

class HomeAdapter(private val role: String?) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val listProduct = ArrayList<HomeModel>()
    fun setData(items: ArrayList<HomeModel>) {
        listProduct.clear()
        listProduct.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: HomeModel) {
            with(binding) {
                title.text = model.name
                Glide.with(itemView.context)
                    .load(model.dp)
                    .into(roundedImageView)

                itemProduct.setOnClickListener {
                    val intent = Intent(itemView.context, HomeDetailActivity::class.java)
                    intent.putExtra(HomeDetailActivity.EXTRA_PRODUCT, model)
                    intent.putExtra(HomeDetailActivity.EXTRA_ROLE, role)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listProduct[position])
    }

    override fun getItemCount(): Int = listProduct.size


}