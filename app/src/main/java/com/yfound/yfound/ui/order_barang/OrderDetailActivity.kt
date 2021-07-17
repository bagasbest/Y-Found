package com.yfound.yfound.ui.order_barang

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.databinding.ActivityOrderDetailBinding

class OrderDetailActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailBinding? = null
    private lateinit var adapter: OrderCartAdapter
    private var show:Boolean = true

    private var buyerId: String? = null
    private var buyerName: String? = null
    private var orderDate: String? = null
    private var orderId: String? = null
    private var location: String? = null
    private var status: String? = null
    private var cartList = ArrayList<OrderCartModel2>()
    private var sendQTY = ArrayList<OrderQtyModel>()
    private var totalProduct0: Int = 0
    private var totalProduct1: Int = 0

    override fun onResume() {
        super.onResume()
        totalProduct0 = 0
        totalProduct1= 0
        cartList.clear()
        sendQTY.clear()
        populateView()
        initRecyclerview()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Order Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // show hide header
        showHideHeader()
    }

    @SuppressLint("SetTextI18n")
    private fun populateView() {
        val data = intent.getParcelableExtra<OrderModel>(EXTRA_ORDER)
        buyerId = data?.buyerId
        buyerName = data?.buyerName
        orderId = data?.orderId
        orderDate = data?.orderDate
        location = data?.location
        status  = data?.status


        for(i in data?.cart?.indices!!) {
            cartList.add(i, OrderCartModel2(data.cart!![i].name, data.cart!![i].quantity, data.cart!![i].dp))
            sendQTY.add(i, OrderQtyModel(data.cart!![i].quantity, "0"))
            totalProduct0 += Integer.parseInt(data.cart!![i].quantity.toString())
        }

        binding?.buyerName?.text = "Sales: $buyerName"
        binding?.orderId?.text = "Order ID: $orderId"
        binding?.orderDate?.text = "Waktu order: $orderDate"
        binding?.location?.text = "Tujuan pengiriman: $location"
        if(status == "not shipped") {
            binding?.status?.text = "Status pengiriman: Belum Dikirim"
        }
        else {
            binding?.status?.text = "Status pengiriman: Sudah Dikirim"
        }

        // klik kirim barang
        clickSendProduct(data)

    }

    private fun showHideHeader() {
        binding?.showHide?.setOnClickListener {
            if(show) {
                binding?.constraintL?.visibility = View.GONE
                show = false
            } else {
                binding?.constraintL?.visibility = View.VISIBLE
                show = true
            }
        }
    }

    private fun clickSendProduct(data: OrderModel) {
        binding?.sendBtn?.setOnClickListener {

            for(i in 0 until sendQTY.size) {
                totalProduct1 += Integer.parseInt(sendQTY[i].qtyHold.toString())
            }

            // make percentage
            val percentage: Double =  ((totalProduct0.toDouble()-totalProduct1.toDouble())/totalProduct0.toDouble() * 100.0)

            val intent = Intent (this, OrderDetailSendActivity::class.java)
            intent.putExtra(OrderDetailSendActivity.EXTRA_ORDER, data)
            intent.putExtra(OrderDetailSendActivity.EXTRA_PRODUCT, sendQTY)
            intent.putExtra(OrderDetailSendActivity.EXTRA_PERCENTAGE, String.format("%.0f", percentage))
            startActivity(intent)
        }
    }

    private fun initRecyclerview() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            Firebase
                .firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    if(it["role"].toString() == "admin") {
                        binding?.rvOrder?.layoutManager = LinearLayoutManager(this)
                        adapter = OrderCartAdapter("admin", sendQTY, intent.getStringExtra(EXTRA_STATUS))
                        binding?.rvOrder?.adapter = adapter
                        adapter.setData(cartList)
                        if(intent.getStringExtra(EXTRA_STATUS) == "not shipped") {
                            binding?.sendBtn?.visibility = View.VISIBLE
                        }
                    } else {
                        binding?.rvOrder?.layoutManager = LinearLayoutManager(this)
                        adapter = OrderCartAdapter(
                            "not admin",
                            sendQTY,
                            intent.getStringExtra(EXTRA_STATUS)
                        )
                        binding?.rvOrder?.adapter = adapter
                        adapter.setData(cartList)
                    }
                }
        }
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
        const val EXTRA_STATUS = "status"
        const val EXTRA_ORDER = "order"
    }
}