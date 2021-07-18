package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.databinding.ActivityAllUserBinding

class AllUserActivity : AppCompatActivity() {

    var binding: ActivityAllUserBinding? = null
    private lateinit var adapter: VerifikasiUserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Semua Pengguna Terdaftar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        binding?.rvVerification?.layoutManager = LinearLayoutManager(this)
        adapter = VerifikasiUserAdapter("active")
        binding?.rvVerification?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[VerifikasiUserViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setActiveUser()
        viewModel.getWaitingUser().observe(this, { salesList ->
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