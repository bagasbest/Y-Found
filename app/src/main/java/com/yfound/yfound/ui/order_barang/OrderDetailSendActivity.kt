package com.yfound.yfound.ui.order_barang

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yfound.yfound.HomepageActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityOrderDetailSendBinding
import com.yfound.yfound.ui.home.keranjang_belanjaan.CartModel2
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailSendActivity : AppCompatActivity() {

    private var binding: ActivityOrderDetailSendBinding? = null
    private val galleryPhotoRequestCode = 1001
    private var invoiceDp: String? = null

    private var buyerId: String? = null
    private var buyerName: String? = null
    private var orderDate: String? = null
    private var orderId: String? = null
    private var location: String? = null
    private var status: String? = null
    private var percentage: String? = null
    private var cartList = ArrayList<OrderCartModel>()
    private var sendQTY = ArrayList<OrderQtyModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailSendBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Order Detail Pengiriman"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<OrderModel>(EXTRA_ORDER) as OrderModel
        buyerId = data.buyerId
        buyerName = data.buyerName
        orderId = data.orderId
        orderDate = data.orderDate
        location = data.location
        status = data.status

        for (i in data.cart?.indices!!) {
            cartList.add(i, OrderCartModel(data.cart!![i].name, data.cart!![i].quantity, data.cart!![i].dp))
        }

        val data2 = intent.getParcelableArrayListExtra<OrderQtyModel>(EXTRA_PRODUCT)
        if (data2 != null) {
            sendQTY.addAll(data2)
        }

        percentage = intent.getStringExtra(EXTRA_PERCENTAGE)

        // tambahkan bukti pengiriman
        binding?.productDp?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(galleryPhotoRequestCode)
        }

        // kirim barang
        binding?.saveBtn?.setOnClickListener {

            if(invoiceDp == null) {
                Toast.makeText(this, "Bukti pengiriman barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmDialog()
        }

    }

    private fun showConfirmDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Konfirmasi pengiriman barang")
        alertDialog.setIcon(R.drawable.ic_baseline_warning_24)
        alertDialog.setMessage("Apakah anda yakin ingin melakukan pengiriman dari Order ini ?\n\nPersentase pengiriman barang $percentage%\n\nJika ada stok yang kurang, aplikasi akan membuatkan Order baru berdasarkan stok barang yang kurang")
        alertDialog.setPositiveButton("Kirim") { dialog, _ ->
            dialog.dismiss()
            sendProduct()
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun sendProduct() {


        for (i in 0 until cartList.size) {
            cartList[i].quantity = sendQTY[i].quantity
        }

        val timeInMillis = System.currentTimeMillis().toString()
        // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
        @SuppressLint("SimpleDateFormat") val getDate =
            SimpleDateFormat("dd MMMM yyyy")
        val format: String = getDate.format(Date())

        @SuppressLint("SimpleDateFormat") val getDate2 =
            SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
        val format2: String = getDate2.format(Date())

        val shippedOrder = mapOf(
            "status" to "shipped",
            "orderDate" to format2,
            "cart" to cartList,
        )

        val delivery = hashMapOf(
            "deliveryId" to timeInMillis,
            "deliveryDate" to format,
            "location" to location,
            "locationQuery" to location?.toLowerCase(Locale.getDefault()),
            "dp" to invoiceDp
        )

        binding?.progressBar?.visibility = View.VISIBLE
        orderId?.let {
            Firebase
                .firestore
                .collection("order")
                .document(it)
                .update(shippedOrder)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {

                        Firebase
                            .firestore
                            .collection("delivery")
                            .document(timeInMillis)
                            .set(delivery)
                            .addOnCompleteListener { process ->
                                if(process.isSuccessful) {

                                    if(percentage == "100") {
                                        binding?.progressBar?.visibility = View.GONE
                                        showSuccessDelivery("100")
                                    }else {
                                        createNewOrder()
                                    }

                                }
                                else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDelivery()
                                }
                            }
                    }
                    else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDelivery()
                    }
                }
        }
    }

    private fun createNewOrder() {
        val newOrderCart = ArrayList<CartModel2>()

        for (i in 0 until cartList.size) {
            if (sendQTY[i].qtyHold?.toInt()!! > 0) {
                val model = CartModel2()
                model.dp = cartList[i].dp
                model.name = cartList[i].name
                model.quantity = sendQTY[i].qtyHold

                newOrderCart.add(model)
            }
        }


        val timeInMillis = System.currentTimeMillis().toString()

        // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
        @SuppressLint("SimpleDateFormat") val getDate =
            SimpleDateFormat("dd MMMM yyyy, hh:mm:ss")
        val format: String = getDate.format(Date())


        val data = hashMapOf(
            "buyerName" to buyerName,
            "buyerId" to buyerId,
            "orderId" to timeInMillis,
            "orderDate" to format,
            "cart" to newOrderCart,
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
                    binding?.progressBar?.visibility = View.GONE
                    showSuccessDelivery(percentage.toString())
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDelivery()
                }
            }
    }

    private fun showFailureDelivery() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Barang tidak terkirim")
        alertDialog.setIcon(R.drawable.ic_baseline_clear_24)
        alertDialog.setMessage("Terdapat kendala ketika ingin mengirim barang, silahkan coba beberapa saat lagi")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun showSuccessDelivery(percentage: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Status Barang Telah Dikirim")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        if (percentage == "100") {
            alertDialog.setMessage("Status Order ini telah berubah menjadi terkirim, barang terkirim 100%, transaksi selesai")
        } else {
            alertDialog.setMessage("Status Order ini telah berubah menjadi terkirim, barang terkirim $percentage%, barang yang belum memenuhi kuantitas akan dibuatkan Order baru")
        }
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            startActivity(Intent(this, HomepageActivity::class.java))
        }
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == galleryPhotoRequestCode) {
                data?.data?.let { uploadImageToDatabase(it) }

                // tampilkan gambar produk ke halaman AddProductActivity
                binding?.productDp?.let {
                    Glide.with(this)
                        .load(data?.data)
                        .into(it)
                }
            }
        }
    }

    private fun uploadImageToDatabase(data: Uri) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        binding?.progressBar?.visibility = View.VISIBLE
        val imageFileName = "invoice/image_" + System.currentTimeMillis() + ".png"


        mStorageRef.child(imageFileName).putFile(data)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        binding?.progressBar?.visibility = View.GONE
                        invoiceDp = uri.toString()
                        binding?.addHint?.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Failed added dp",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.addHint?.visibility = View.VISIBLE
                    }
            }
            .addOnFailureListener {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Failed added dp",
                    Toast.LENGTH_SHORT
                ).show()
                binding?.addHint?.visibility = View.VISIBLE
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
        const val EXTRA_ORDER = "order"
        const val EXTRA_PRODUCT = "product"
        const val EXTRA_PERCENTAGE = "percentage"
    }
}