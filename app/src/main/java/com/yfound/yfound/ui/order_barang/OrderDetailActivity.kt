package com.yfound.yfound.ui.order_barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yfound.yfound.databinding.ActivityOrderDetailBinding
import com.yfound.yfound.ui.home.keranjang_belanjaan.CartModel2

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null

    private var buyerId: String? = null
    private var buyerName: String? = null
    private var orderDate: String? = null
    private var orderId: String? = null
    private var location: String? = null
    private var cart = ArrayList<CartModel2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Order Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<OrderModel>(EXTRA_ORDER)
        buyerId = data?.buyerId
        buyerName = data?.buyerName
        orderId = data?.orderId
        orderDate = data?.orderDate
        location = data?.location

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
        const val EXTRA_ORDER = "order"
    }
}