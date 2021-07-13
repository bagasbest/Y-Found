package com.yfound.yfound.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yfound.yfound.databinding.FragmentHomeBinding
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter

    override fun onResume() {
        super.onResume()
        initRecyclerView()
        initViewModel("all")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // CARI PRODUK
        searchProduct()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(activity, AddProductActivity::class.java))
        }

    }

    private fun searchProduct() {
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun afterTextChanged(edit: Editable) {
                if (edit.isNotEmpty()) {
                    // Business logic for search here
                    val query = edit.toString().toLowerCase(Locale.getDefault())
                    initRecyclerView()
                    initViewModel(query)
                }
                else {
                    initRecyclerView()
                    initViewModel("all")
                }
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvProduct.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = HomeAdapter()
        binding.rvProduct.adapter = adapter
    }

    private fun initViewModel(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]

        if(query == "all") {
            viewModel.setAllProduct()
        }
        else {
            viewModel.setProductByQuery(query)
        }

        viewModel.getAllProduct().observe(viewLifecycleOwner, { productList ->
            if(productList.size > 0) {
                binding.noData.visibility = View.GONE
                adapter.setData(productList)
            }
            else {
                binding.noData.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}