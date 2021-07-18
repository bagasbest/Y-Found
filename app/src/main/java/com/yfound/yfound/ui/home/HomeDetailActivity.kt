package com.yfound.yfound.ui.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.HomepageActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityHomeDetailBinding
import com.yfound.yfound.databinding.PopupQuantityBinding
import java.util.*


class HomeDetailActivity : AppCompatActivity() {

    private var binding: ActivityHomeDetailBinding? = null
    var name: String? = null
    var dp : String? = null
    var addedDate: String? = null
    var productId: String? = null
    private lateinit var data: HomeModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = intent.getParcelableExtra<HomeModel>(EXTRA_PRODUCT) as HomeModel
        name = data.name
        addedDate = data.addedDate
        productId = data.productId
        dp = data.dp

        binding?.dp?.let {
            Glide
                .with(this)
                .load(dp)
                .into(it)
        }

        binding?.name?.text = name
        binding?.addedAt?.text = "Ditambahkan oleh admin pada: $addedDate"

        // KLIK ORDER BARANG
        binding?.buyBtn?.setOnClickListener {
            addQuantityProduct()
        }

    }

    private fun addQuantityProduct() {
        val binding: PopupQuantityBinding = PopupQuantityBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        dialog.setCanceledOnTouchOutside(false)

        binding.dismissBtn.setOnClickListener {dialog.dismiss()}

        binding.addCart.setOnClickListener {
            val quantity = binding.totalProduct.text.toString().trim()

            if(quantity.isEmpty()) {
                binding.totalProduct.error = "Kuantitas barang tidak boleh kosong"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            val timeInMillis = System.currentTimeMillis().toString()
            val buyerId = FirebaseAuth.getInstance().currentUser?.uid

            val data = hashMapOf(
                "productId" to productId,
                "name" to name,
                "dp" to dp,
                "quantity" to quantity,
                "buyerId" to buyerId,
                "cartId" to timeInMillis,
            )

            // SIMPAN KUANTITAS PRODUK KEDALAM DATABASE
            Firebase
                .firestore
                .collection("cart")
                .document(timeInMillis)
                .set(data)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Berhasil menambahkan barang ke keranjang", Toast.LENGTH_SHORT).show()
                        binding.totalProduct.text?.clear()
                        dialog.dismiss()
                    }
                    else {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Gagal menambahkan kuantitas barang", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_edit_delete, menu)
        // jika bukan admin maka sembunyikan ikon
        val item = menu?.findItem(R.id.menu_edit)?.setVisible(false)
        val item2 = menu?.findItem(R.id.menu_delete)?.setVisible(false)

        val role = intent.getStringExtra(EXTRA_ROLE)

        if (role == "admin") {
            item?.isVisible = true
            item2?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.menu_delete) {
            showConfirmDeleteProduct()
        }
        else if(item.itemId == R.id.menu_edit) {
            val intent = Intent(this, HomeEditProductActivity::class.java)
            intent.putExtra(HomeEditProductActivity.EXTRA_PRODUCT, data)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showConfirmDeleteProduct() {
        val dialog = this.let { it1 -> AlertDialog.Builder(it1) }
        dialog.setTitle("Konfirmasi Hapus Barang")
        dialog.setMessage("Apakah anda yakin ingin Menghapus barang: $name ?")
        dialog.setIcon(R.drawable.ic_baseline_warning_24)
        dialog.setPositiveButton("YA") { it2, _ ->

            productId?.let {
                Firebase
                    .firestore
                    .collection("product")
                    .document(it)
                    .delete()
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(this,"Barang $name, berhasil dihapus", Toast.LENGTH_SHORT).show()

                            // go to homepage activity
                            val intent = Intent(this, HomepageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            it2.dismiss()
                            startActivity(intent)
                            finish()
                        }
                        else {
                            Toast.makeText(this,"Barang $name, tidak berhasil dihapus, silahkan coba lagi kemudian", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
        dialog.setNegativeButton("Tidak") { dialogs, _ ->
            dialogs.dismiss()
        }
        dialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_ROLE = "role"
        const val EXTRA_PRODUCT = "product"
    }
}