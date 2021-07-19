package com.yfound.yfound.ui.order_barang.status

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.databinding.FragmentDoneBinding
import com.yfound.yfound.ui.order_barang.OrderAdapter
import com.yfound.yfound.ui.order_barang.OrderViewModel
import java.util.*


class DoneFragment : Fragment() {

    private var binding: FragmentDoneBinding? = null
    private lateinit var adapter: OrderAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("all")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoneBinding.inflate(layoutInflater,container,false)
        // CARI PRODUK
        searchProduct()
        return binding?.root
    }

    private fun searchProduct() {
        binding?.searchIv?.setOnClickListener {
            val edit = binding?.searchEt?.text.toString().trim()

            if (edit.isNotEmpty()) {
                // Business logic for search here
                val query = edit.toLowerCase(Locale.getDefault())
                initRecyclerView()
                initViewModel(query)
            } else {
                initRecyclerView()
                initViewModel("all")
            }

        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding?.rvDone?.layoutManager = layoutManager
        adapter = OrderAdapter()
        binding?.rvDone?.adapter = adapter
    }

    private fun initViewModel(query: String) {
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[OrderViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE

        if(query == "all") {
            viewModel.setAllOrderByStatus("shipped")
        }
        else {
            viewModel.setSearchedByStatus("shipped", query)
        }
        viewModel.getAllOrder().observe(viewLifecycleOwner, {
            if(it.size > 0) {
                binding?.noData?.visibility = View.GONE
                adapter.setData(it)
            }
            else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}