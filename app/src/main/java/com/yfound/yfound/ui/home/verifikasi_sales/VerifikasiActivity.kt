package com.yfound.yfound.ui.home.verifikasi_sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.databinding.ActivityVerifikasiBinding

class VerifikasiActivity : AppCompatActivity() {

    var binding: ActivityVerifikasiBinding? = null
    private lateinit var adapter: VerifikasiAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Verifikasi Data Sales"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }

    private fun initRecyclerView() {
        binding?.rvVerification?.layoutManager = LinearLayoutManager(this)
        adapter = VerifikasiAdapter()
        binding?.rvVerification?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[VerifikasiViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setAllSales()
        viewModel.getAllSales().observe(this, { salesList ->
            if(salesList.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(salesList)
            }
            else {
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