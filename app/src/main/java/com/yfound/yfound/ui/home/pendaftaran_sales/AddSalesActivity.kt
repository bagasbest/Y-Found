package com.yfound.yfound.ui.home.pendaftaran_sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yfound.yfound.databinding.ActivityAddSalesBinding

class AddSalesActivity : AppCompatActivity() {

    private var binding: ActivityAddSalesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSalesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Mendaftar Menjadi Sales"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)




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