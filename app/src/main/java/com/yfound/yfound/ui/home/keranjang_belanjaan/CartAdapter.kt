package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
                    val builder = AlertDialog.Builder(itemView.context)
                    builder.setTitle("Konfirmasi menghapus barang")
                    builder.setMessage("Apakah anda yakin ingin menghapus barang ${cartModel.name} dari keranjang ?")
                    builder.setPositiveButton("Ya") { dialog, _ ->
                        dialog.dismiss()
                        cartModel.cartId?.let { it1 ->
                            Firebase
                                .firestore
                                .collection("cart")
                                .document(it1)
                                .delete()
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful) {
                                        listCart.remove(listCart[adapterPosition])
                                        notifyDataSetChanged()
                                        Toast.makeText(itemView.context, "Menghapus ${cartModel.name}", Toast.LENGTH_SHORT).show()
                                    }
                                    else {
                                        Toast.makeText(itemView.context, "Gagal menghapus ${cartModel.name}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                    builder.setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                }
            }
        }
    }
}