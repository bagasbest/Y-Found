package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityCartBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartActivity : AppCompatActivity() {

    private var binding: ActivityCartBinding? = null
    private lateinit var adapter: CartAdapter
    private var uid: String? = null
    private var cartList = ArrayList<CartModel>()
    private var buyerName: String? = null

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Keranjang Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

        // GET BUYER NAME
        getBuyerName()

        // ORDER SEMUA BARANG
        clickOrder()

    }

    private fun getBuyerName() {
        uid?.let {
            Firebase
                .firestore
                .collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { data ->
                    buyerName = data["name"].toString()
                }
        }
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun clickOrder() {
        binding?.orderBtn?.setOnClickListener {

            val cartList2 = ArrayList<CartModel2>()

            binding?.orderBtn?.text = "Silahkan tunggu"
            binding?.orderBtn?.setBackgroundColor(R.color.mermud)

            for (i in 0 until cartList.size) {
                val model = CartModel2()
                model.name = cartList[i].name
                model.quantity = cartList[i].quantity

                cartList2.add(model)
            }

            val timeInMillis = System.currentTimeMillis().toString()

            // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
            @SuppressLint("SimpleDateFormat") val getDate =
                SimpleDateFormat("dd - MMMM - yyyy, hh:mm:ss")
            val format: String = getDate.format(Date())

            val data = hashMapOf(
                "buyerName" to buyerName,
                "buyerId" to uid,
                "orderId" to timeInMillis,
                "orderDate" to format,
                "cart" to cartList2
            )

            Firebase
                .firestore
                .collection("order")
                .document(timeInMillis)
                .set(data)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        deleteAllCart()
                    }
                    else {
                        binding?.orderBtn?.text = "Order Barang pada keranjang"
                        binding?.orderBtn?.setBackgroundColor(R.color.primary)
                    }
                }
        }
    }

    private fun deleteAllCart() {
        for (i in 0 until cartList.size) {
            cartList[i].cartId?.let {
                Firebase
                    .firestore
                    .collection("cart")
                    .document(it)
                    .delete()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Log.d(i.toString(), "Berhasil")
                        } else {
                            Log.d(i.toString(), "Gagal")
                        }
                    }
            }
        }

        binding?.orderBtn?.visibility = View.GONE
        binding?.recyclerView?.visibility = View.GONE
        binding?.noData?.visibility = View.VISIBLE
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.layoutManager = layoutManager
        adapter = CartAdapter()
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel() {
        binding?.progressBar?.visibility = View.VISIBLE
        val viewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            )[CartViewModel::class.java]


        viewModel.setCartByBuyerId(uid!!)
        viewModel.getAllCart().observe(this, { data ->
            if (data.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(data)
                binding?.orderBtn?.visibility = View.VISIBLE
                cartList.addAll(data)
            } else {
                binding?.noData?.visibility = View.VISIBLE
                binding?.orderBtn?.visibility = View.GONE
            }
            binding?.progressBar?.visibility = View.GONE
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}