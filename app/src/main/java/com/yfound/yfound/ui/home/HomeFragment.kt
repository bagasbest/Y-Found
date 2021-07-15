package com.yfound.yfound.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.HomepageActivity
import com.yfound.yfound.LoginActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.FragmentHomeBinding
import com.yfound.yfound.ui.home.keranjang_belanjaan.CartActivity
import com.yfound.yfound.ui.home.pendaftaran_sales.AddSalesActivity
import com.yfound.yfound.ui.home.verifikasi_sales.VerifikasiActivity
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter: HomeAdapter
    private var role: String? = null

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

        // CEK APAKAH ADMIN, SALES, ATAU USER BIASA
        checkRole()

        return binding.root
    }

    private fun checkRole() {
        val uid = FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid

        if (uid != null) {
            Firebase
                .firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    role = it["role"].toString()
                    if (role == "admin") {
                        binding.fabAddProduct.visibility = View.VISIBLE
                    }
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(activity, AddProductActivity::class.java))
        }

        binding.menu.setOnClickListener {
            if (role == "admin") {
                showAdminMenu()
            } else if (role == "sales") {
                showSalesMenu()
            } else if (role == "user" || role == "waiting") {
                showUserMenu()
            }
        }

    }

    private fun showAdminMenu() {
        val options = arrayOf("Verifikasi Sales", "Logout")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Menu Pilihan")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // VERIFIKASI SALES
                    dialog.dismiss()
                    startActivity(Intent(activity, VerifikasiActivity::class.java))

                }
                1 -> {
                    // LOGOUT
                    dialog.dismiss()
                    clickLogout()
                }
            }
        }
        builder.create().show()
    }

    private fun showSalesMenu() {
        val options = arrayOf("Keranjang Barang", "Logout")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Menu Pilihan")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // KERANJANG BELANJAAN
                    dialog.dismiss()
                    startActivity(Intent(activity, CartActivity::class.java))
                }
                1 -> {
                    // LOGOUT
                    dialog.dismiss()
                    clickLogout()
                }
            }
        }
        builder.create().show()
    }

    private fun showUserMenu() {
        val options = arrayOf("Menjadi Sales", "Logout")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Menu Pilihan")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // PENDAFTARAN SALES
                    dialog.dismiss()
                    if (role == "waiting") {
                        showWaitingDialog()
                    } else if (role == "user") {
                        startActivity(Intent(activity, AddSalesActivity::class.java))
                    }
                }
                1 -> {
                    // LOGOUT
                    dialog.dismiss()
                    clickLogout()
                }
            }
        }
        builder.create().show()
    }


    private fun showWaitingDialog() {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle("Registrasi Hanya Boleh Sekali")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("Anda berhasil terdaftar sebagai sales pada aplikasi\n\nSilahkan tunggu beberapa saat, admin Y Found akan memverifikasi data anda, dan sesaat setelahnya akan dapat melakukan order")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun clickLogout() {
        val dialog = context?.let { it1 -> AlertDialog.Builder(it1) }
        dialog?.setTitle("Konfirmasi Logout")
        dialog?.setMessage("Apakah anda yakin ingin logout ?")
        dialog?.setIcon(R.drawable.ic_baseline_logout_24)
        dialog?.setPositiveButton("YA") { it2, _ ->
            // sign out dari firebase autentikasi
            FirebaseAuth.getInstance().signOut()
            // go to login activity
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            it2.dismiss()
            startActivity(intent)
            activity?.finish()
        }
        dialog?.setNegativeButton("Tidak") { dialogs, _ ->
            dialogs.dismiss()
        }
        dialog?.show()
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
                } else {
                    initRecyclerView()
                    initViewModel("all")
                }
            }
        })
    }

    private fun initRecyclerView() {
        binding.rvProduct.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = HomeAdapter()
        binding.rvProduct.adapter = adapter
    }

    private fun initViewModel(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        val viewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            )[HomeViewModel::class.java]

        if (query == "all") {
            viewModel.setAllProduct()
        } else {
            viewModel.setProductByQuery(query)
        }

        viewModel.getAllProduct().observe(viewLifecycleOwner, { productList ->
            if (productList.size > 0) {
                binding.noData.visibility = View.GONE
                adapter.setData(productList)
            } else {
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