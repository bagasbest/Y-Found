package com.yfound.yfound.ui.pengiriman_barang

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yfound.yfound.databinding.ActivityDeliveryDetailBinding

class DeliveryDetailActivity : AppCompatActivity() {

    private var binding: ActivityDeliveryDetailBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Barang Terkirim"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<DeliveryModel>(EXTRA_DELIVERY) as DeliveryModel
        binding?.deliveryId?.text = "Kode Pengiriman: " + data.deliveryId
        binding?.date?.text = "Tanggal pengiriman: " + data.deliveryDate
        binding?.location?.text = "Tujuan pengiriman: " + data.location

        binding?.dp?.let {
            Glide.with(this)
                .load(data.dp)
                .into(it)
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
        const val EXTRA_DELIVERY = "delivery"
    }
}