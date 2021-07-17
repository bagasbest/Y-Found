package com.yfound.yfound.ui.order_barang.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.R
import com.yfound.yfound.databinding.FragmentDoneBinding
import com.yfound.yfound.databinding.FragmentProgressBinding
import com.yfound.yfound.ui.order_barang.OrderAdapter
import com.yfound.yfound.ui.order_barang.OrderViewModel


class DoneFragment : Fragment() {

    private var binding: FragmentDoneBinding? = null
    private lateinit var adapter: OrderAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoneBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    private fun initRecyclerView() {
        binding?.rvDone?.layoutManager = LinearLayoutManager(activity)
        adapter = OrderAdapter()
        binding?.rvDone?.adapter = adapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[OrderViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE
        viewModel.setAllOrderByStatus("shipped")
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