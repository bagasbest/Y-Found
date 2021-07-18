package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
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

            val location = binding?.location?.text.toString().trim()
            if (location.isEmpty()) {
                binding?.location?.error = "Masukkan lokasi tujan pengiriman terlebih dahulu"
                return@setOnClickListener
            }

            val cartList2 = ArrayList<CartModel2>()

            binding?.orderBtn?.text = "Silahkan tunggu"
            binding?.orderBtn?.setBackgroundColor(R.color.mermud)

            // get all product from cart
            uid?.let { it1 ->
                Firebase
                    .firestore
                    .collection("cart")
                    .whereEqualTo("buyerId", it1)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val model2 = CartModel2()
                            model2.name = document.data["name"].toString()
                            model2.quantity = document.data["quantity"].toString()
                            model2.dp = document.data["dp"].toString()

                            cartList2.add(model2)
                        }

                        val timeInMillis = System.currentTimeMillis().toString()

                        // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
                        @SuppressLint("SimpleDateFormat") val getDate =
                            SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
                        val format: String = getDate.format(Date())

                        val data = hashMapOf(
                            "buyerName" to buyerName,
                            "buyerId" to uid,
                            "orderId" to timeInMillis,
                            "orderDate" to format,
                            "cart" to cartList2,
                            "location" to location,
                            "status" to "not shipped"
                        )

                        Firebase
                            .firestore
                            .collection("order")
                            .document(timeInMillis)
                            .set(data)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    deleteAllCart(cartList2)
                                } else {
                                    showAlertDialog("false")
                                    binding?.orderBtn?.text = "Order Barang pada keranjang"
                                    binding?.orderBtn?.setBackgroundColor(R.color.primary)
                                }
                            }
                    }
            }
        }
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun deleteAllCart(cartList2: ArrayList<CartModel2>) {
        for (i in 0 until cartList2.size) {
            cartList[i].cartId?.let {
                Firebase
                    .firestore
                    .collection("cart")
                    .document(it)
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if(i==0) {
                                showAlertDialog("Sukses")
                                binding?.orderBtn?.visibility = View.GONE
                                binding?.recyclerView?.visibility = View.GONE
                                binding?.noData?.visibility = View.VISIBLE
                                binding?.textInputLayout10?.visibility = View.GONE
                            }
                        } else {
                            if(i==0) {
                                showAlertDialog("Gagal")
                                binding?.orderBtn?.text = "Order Barang pada keranjang"
                                binding?.orderBtn?.setBackgroundColor(R.color.primary)
                            }
                        }
                    }
            }
        }
    }

    private fun showAlertDialog(result: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("$result Order Barang")
        builder.setMessage("Anda $result mengorder barang dari keranjang")
        if (result == "Sukses") {
            builder.setIcon(R.drawable.ic_baseline_check_circle_24)
        } else {
            builder.setIcon(R.drawable.ic_baseline_clear_24)
        }
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
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
                binding?.textInputLayout10?.visibility = View.VISIBLE
                binding?.orderBtn?.visibility = View.VISIBLE
                cartList.addAll(data)
            } else {
                binding?.noData?.visibility = View.VISIBLE
                binding?.textInputLayout10?.visibility = View.GONE
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