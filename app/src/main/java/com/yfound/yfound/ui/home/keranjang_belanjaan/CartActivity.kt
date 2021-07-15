package com.yfound.yfound.ui.home.keranjang_belanjaan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {

    private var binding: ActivityCartBinding? = null
    private lateinit var adapter: CartAdapter
    private var uid: String? = null


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


        // CEK ROLE = ADMIN, SALES, ATAU USER BIASA
        checkRole()
    }

    private fun checkRole() {
        if (uid != null) {
            Firebase
                .firestore
                .collection("users")
                .document(uid!!)
                .get()
                .addOnSuccessListener {
                    when (it["role"].toString()) {
                        "admin" -> {
                            initRecyclerView()
                            initViewModel("admin")
                            binding?.orderBtn?.visibility = View.VISIBLE
                        }
                        "sales" -> {
                            initRecyclerView()
                            initViewModel("sales")
                            binding?.orderBtn?.visibility = View.VISIBLE
                        }
                        else -> {
                            binding?.noData?.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.layoutManager = layoutManager
        adapter = CartAdapter()
        binding?.recyclerView?.adapter = adapter
    }

    private fun initViewModel(role: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        val viewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            )[CartViewModel::class.java]

        if (role == "admin") {
            viewModel.setAllCart()
        } else {
            viewModel.setCartByBuyerId(uid!!)
        }

        viewModel.getAllCart().observe(this, { cartList ->
            if (cartList.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(cartList)
            } else {
                binding?.noData?.visibility = View.VISIBLE
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