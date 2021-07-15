package com.yfound.yfound.ui.order_barang.status

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yfound.yfound.R
import com.yfound.yfound.databinding.FragmentDoneBinding


class DoneFragment : Fragment() {

    private var binding: FragmentDoneBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoneBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}