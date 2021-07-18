package com.yfound.yfound.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yfound.yfound.HomepageActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityHomeEditProductBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeEditProductActivity : AppCompatActivity() {

    var binding: ActivityHomeEditProductBinding? = null
    private val galleryPhotoRequestCode = 1001
    private var productDp: String? = null
    private lateinit var data: HomeModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeEditProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Edit Barang"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = intent.getParcelableExtra<HomeModel>(EXTRA_PRODUCT) as HomeModel
        binding?.nameEt?.setText(data.name)

        productDp = data.dp

        binding?.productDp?.let {
            Glide.with(this)
                .load(data.dp)
                .into(it)
        }

        binding?.addHint?.visibility = View.GONE


        // upload product dp
        clickImage()

        // click save btn
        clickSaveProduct()

    }

    private fun clickSaveProduct() {
        binding?.saveBtn?.setOnClickListener {
            val name = binding?.nameEt?.text.toString().trim()

            if(name.isEmpty()) {
                binding?.nameEt?.error = "Nama produk tidak boleh kosong"
                return@setOnClickListener
            }
            else if(productDp == null) {
                Toast.makeText(this, "Foto barang tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SIMPAN DATA BARANG KE DATABASE
            saveProductToDatabase(name)
        }
    }

    private fun saveProductToDatabase(name: String) {
        binding?.progressBar?.visibility = View.VISIBLE

        // ambil tanggal hari ini dengan format: dd - MMM - yyyy, HH:mm:ss
        @SuppressLint("SimpleDateFormat") val getDate =
            SimpleDateFormat("dd - MMMM - yyyy")
        val format: String = getDate.format(Date())

        val update = mapOf(
            "name" to name.lowercase(Locale.getDefault()),
            "dp" to productDp,
            "addedDate" to format,
        )

        data.productId?.let {
            Firebase
                .firestore
                .collection("product")
                .document(it)
                .update(update)
                .addOnCompleteListener { data2 ->
                    if (data2.isSuccessful) {
                        binding?.progressBar?.visibility = View.GONE
                        showSuccessDialog(name)
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDialog(name)
                    }
                }
        }

    }

    private fun showSuccessDialog(name: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Sukses Mengupdate Barang")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("$name berhasil diperbarui")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            // go to homepage activity
            val intent = Intent(this, HomepageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        alertDialog.show()
    }

    private fun showFailureDialog(name: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Gagal Mengupdate Barang")
        alertDialog.setIcon(R.drawable.ic_baseline_clear_24)
        alertDialog.setMessage("$name gagal diperbarui")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            binding?.nameEt?.setText("")
            binding?.addHint?.visibility = View.VISIBLE
            binding?.productDp?.setImageResource(android.R.color.transparent)
        }
        alertDialog.show()
    }

    private fun clickImage() {
        binding?.productDp?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(galleryPhotoRequestCode)
        }
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
                            this,
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
                    this,
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

    companion object {
        const val EXTRA_PRODUCT = "product"
    }
}