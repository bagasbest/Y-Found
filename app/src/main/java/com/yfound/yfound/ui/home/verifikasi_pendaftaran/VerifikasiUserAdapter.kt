package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yfound.yfound.databinding.ItemVerificationBinding

class VerifikasiUserAdapter(private val status: String) : RecyclerView.Adapter<VerifikasiUserAdapter.ViewHolder>() {

    private val listUser = ArrayList<VerifikasiUserModel>()
    fun setData(items: ArrayList<VerifikasiUserModel>) {
        listUser.clear()
        listUser.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVerificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = listUser.size

    inner class ViewHolder(private val binding: ItemVerificationBinding) : RecyclerView.ViewHolder (binding.root) {
        fun bind(model: VerifikasiUserModel) {
            with(binding) {
                name.text = model.name
                view.setOnClickListener {
                    val intent = Intent(itemView.context, VerifikasiUserDetailActivity::class.java)
                    intent.putExtra(VerifikasiUserDetailActivity.EXTRA_USER, model)
                    intent.putExtra(VerifikasiUserDetailActivity.EXTRA_STATUS, status)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }


}