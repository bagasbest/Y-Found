package com.yfound.yfound.ui.pengiriman_barang

import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yfound.yfound.databinding.FragmentDeliveryBinding
import com.yfound.yfound.utils.DatePickerFragment
import java.text.SimpleDateFormat
import java.util.*

class DeliveryFragment : Fragment(), OnDateSetListener {

    private lateinit var deliveryViewModel: DeliveryViewModel
    private var _binding: FragmentDeliveryBinding? = null
    private lateinit var adapter: DeliveryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var show: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        deliveryViewModel =
            ViewModelProvider(this).get(DeliveryViewModel::class.java)

        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)

        initRecyclerview()
        initViewModel("all", "all")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.calendarBtn.setOnClickListener {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.setTargetFragment(this, 0)
            fragmentManager?.let { it1 -> datePickerFragment.show(it1, "DatePicker") }
        }

        binding.allDelivery.setOnClickListener {
            initRecyclerview()
            initViewModel("all", "all")
        }

        binding.showHide.setOnClickListener {
            if(show) {
                binding.constraintLayout.visibility = View.GONE
                show = false
            }
            else {
                binding.constraintLayout.visibility = View.VISIBLE
                show = true
            }
        }

        binding.searchIv.setOnClickListener {
            val edit = binding.searchEt.text.toString().trim()

            if (edit.isNotEmpty()) {
                // Business logic for search here
                val query = edit.toLowerCase(Locale.getDefault())
                initRecyclerview()
                initViewModel("all", query)
            } else {
                initRecyclerview()
                initViewModel("all", "all")
            }

        }

    }

    private fun initRecyclerview() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        binding.rvDelivery.layoutManager = layoutManager
        adapter = DeliveryAdapter()
        binding.rvDelivery.adapter = adapter
    }

    private fun initViewModel(date: String, query: String) {
        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DeliveryViewModel::class.java]

        binding.progressBar.visibility = View.VISIBLE
        if(date == "all" && query == "all") {
            viewModel.setAllDelivery()
        }
        else if (date != "all"){
            viewModel.setDeliveryByDate(date)
        }
        else if(query != "all") {
            viewModel.setDeliveryByQuery(query)
        }
        viewModel.getAllDelivery().observe(viewLifecycleOwner, {
            if(it.size > 0) {
                binding.noData.visibility = View.GONE
                adapter.setData(it)
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


    override fun onDateSet(p0: DatePicker?, year: Int, mon: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = mon
        calendar[Calendar.DAY_OF_MONTH] = day

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        //set untuk TextView
        binding.calendarBtn.text = dateFormat.format(calendar.time)
        initRecyclerview()
        initViewModel(dateFormat.format(calendar.time), "all")
    }

}