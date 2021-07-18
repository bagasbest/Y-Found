package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.databinding.ActivityVerifikasiUserBinding
import com.yfound.yfound.ui.home.verifikasi_sales.VerifikasiViewModel

class VerifikasiUserActivity : AppCompatActivity() {

    var binding: ActivityVerifikasiUserBinding? = null
    private lateinit var adapter: VerifikasiUserAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Verifikasi Akun Pengguna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding?.button?.setOnClickListener {
            startActivity(Intent(this, AllUserActivity::class.java))
        }


    }

    private fun initRecyclerView() {
        binding?.rvVerification?.layoutManager = LinearLayoutManager(this)
        adapter = VerifikasiUserAdapter("waiting")
        binding?.rvVerification?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(
            this, ViewModelProvider.NewInstanceFactory()
        )[VerifikasiUserViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setWaitingUser()
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