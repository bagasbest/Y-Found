package com.yfound.yfound.ui.pengiriman_barang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yfound.yfound.databinding.FragmentDeliveryBinding

class DeliveryFragment : Fragment() {

    private lateinit var deliveryViewModel: DeliveryViewModel
    private var _binding: FragmentDeliveryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        deliveryViewModel =
            ViewModelProvider(this).get(DeliveryViewModel::class.java)

        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}