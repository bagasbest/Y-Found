package com.yfound.yfound.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.databinding.ActivityHomeDetailBinding
import com.yfound.yfound.databinding.PopupQuantityBinding
import java.util.*


class HomeDetailActivity : AppCompatActivity() {

    private var binding: ActivityHomeDetailBinding? = null
    var name: String? = null
    var dp : String? = null
    var addedDate: String? = null
    var productId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<HomeModel>(EXTRA_PRODUCT) as HomeModel
        name = data.name
        addedDate = data.addedDate
        productId = data.productId
        dp = data.dp

        binding?.dp?.let {
            Glide
                .with(this)
                .load(dp)
                .into(it)
        }

        binding?.name?.text = name
        binding?.addedAt?.text = "Ditambahkan oleh admin pada: $addedDate"

        // KLIK ORDER BARANG
        binding?.buyBtn?.setOnClickListener {
            addQuantityProduct()
        }

    }

    private fun addQuantityProduct() {
        val binding: PopupQuantityBinding = PopupQuantityBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)

        binding.dismissBtn.setOnClickListener {dialog.dismiss()}

        binding.addCart.setOnClickListener {
            val quantity = binding.totalProduct.text.toString().trim()

            if(quantity.isEmpty()) {
                binding.totalProduct.error = "Kuantitas barang tidak boleh kosong"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            val timeInMillis = System.currentTimeMillis().toString()
            val buyerId = FirebaseAuth.getInstance().currentUser?.uid

            val data = hashMapOf(
                "productId" to productId,
                "name" to name,
                "dp" to dp,
                "quantity" to quantity,
                "buyerId" to buyerId,
                "cartId" to timeInMillis,
            )

            // SIMPAN KUANTITAS PRODUK KEDALAM DATABASE
            Firebase
                .firestore
                .collection("cart")
                .document(timeInMillis)
                .set(data)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Berhasil menambahkan barang ke keranjang", Toast.LENGTH_SHORT).show()
                        binding.totalProduct.text?.clear()
                        dialog.dismiss()
                    }
                    else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Gagal menambahkan kuantitas barang", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_PRODUCT = "product"
    }
}