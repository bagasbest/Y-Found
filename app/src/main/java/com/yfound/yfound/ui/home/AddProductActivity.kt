package com.yfound.yfound.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityAddProductBinding
import java.text.SimpleDateFormat
import java.util.*


class AddProductActivity : AppCompatActivity() {

    private var binding: ActivityAddProductBinding? = null
    private val galleryPhotoRequestCode = 1001
    private var productDp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Tambah Barang Baru"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // UNGGAH FOTO PRODUK
        binding?.productDp?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(galleryPhotoRequestCode)
        }


        // SIMPAN BARANG BARU
        saveProduct()

    }

    private fun saveProduct() {
        binding?.saveBtn?.setOnClickListener {
            // VERIFIKASI KOLOM YANG DIISI
            verificateFormAddProduct()
        }
    }

    private fun verificateFormAddProduct() {
        val name = binding?.nameEt?.text.toString().trim()

        if (name.isEmpty()) {
            binding?.nameEt?.error = "Nama barang tidak boleh kosong"
            return
        } else if (productDp == null) {
            Toast.makeText(this, "Gambar barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // SIMPAN DATA BARANG KE DATABASE
        saveProductToDatabase(name)
    }

    private fun saveProductToDatabase(name: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        val timeInMillis = System.currentTimeMillis().toString()

        // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
        @SuppressLint("SimpleDateFormat") val getDate =
            SimpleDateFormat("dd - MMMM - yyyy")
        val format: String = getDate.format(Date())

        val data = hashMapOf(
            "name" to name.lowercase(Locale.getDefault()),
            "dp" to productDp,
            "productId" to timeInMillis,
            "addedDate" to format,
        )

        Firebase
            .firestore
            .collection("product")
            .document(timeInMillis)
            .set(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding?.progressBar?.visibility = View.GONE
                    showSuccessDialog(name)
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    showFailureDialog(name)
                }
            }

    }

    private fun showSuccessDialog(name: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Sukses Menambah Barang Baru")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("$name berhasil ditambahkan")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            binding?.nameEt?.setText("")
            binding?.addHint?.visibility = View.VISIBLE
            binding?.productDp?.setImageResource(android.R.color.transparent)
        }
        alertDialog.show()
    }

    private fun showFailureDialog(name: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Gagal Menambah Barang Baru")
        alertDialog.setIcon(R.drawable.ic_baseline_clear_24)
        alertDialog.setMessage("$name gagal ditambahkan")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            binding?.nameEt?.setText("")
            binding?.addHint?.visibility = View.VISIBLE
            binding?.productDp?.setImageResource(android.R.color.transparent)
        }
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == galleryPhotoRequestCode) {
                data?.data?.let { uploadImageToDatabase(it) }

                // tampilkan gambar produk ke halaman AddProductActivity
                binding?.productDp?.let {
                    Glide.with(this)
                        .load(data?.data)
                        .into(it)
                }
            }
        }
    }

    private fun uploadImageToDatabase(data: Uri) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        binding?.progressBar?.visibility = View.VISIBLE
        val imageFileName = "product/image_" + System.currentTimeMillis() + ".png"


        mStorageRef.child(imageFileName).putFile(data)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        binding?.progressBar?.visibility = View.GONE
                        productDp = uri.toString()
                        binding?.addHint?.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            this@AddProductActivity,
                            "Failed added product dp",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.addHint?.visibility = View.VISIBLE
                    }
            }
            .addOnFailureListener {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(
                    this@AddProductActivity,
                    "Failed added product dp",
                    Toast.LENGTH_SHORT
                ).show()
                binding?.addHint?.visibility = View.VISIBLE
            }
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