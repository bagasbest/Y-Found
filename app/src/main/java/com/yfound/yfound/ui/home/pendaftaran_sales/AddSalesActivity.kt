package com.yfound.yfound.ui.home.pendaftaran_sales

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.yfound.yfound.HomepageActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityAddSalesBinding

class AddSalesActivity : AppCompatActivity() {

    private var binding: ActivityAddSalesBinding? = null
    private var selfPhopto: String? = null
    private var ktp: String? = null
    private val selfPhotoCode: Int = 101
    private val ktpCode: Int = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSalesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Mendaftar Menjadi Sales"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // INPUT FOTO FORMAL
        binding?.selfPhoto?.setOnClickListener {
            inputFormalPhoto()
        }

        // INPUT FOTO KTP
        binding?.ktp?.setOnClickListener {
            inputKtp()
        }

        // KLIK TOMBOL KONFIRMASI
        binding?.confirmBtn?.setOnClickListener {
            clickConfirmButton()
        }

    }

    private fun clickConfirmButton() {
        val name = binding?.nameEt?.text.toString().trim()
        val phone = binding?.phoneEt?.text.toString().trim()

        when {
            name.isEmpty() -> {
                binding?.nameEt?.error = "Nama lengkap tidak boleh kosong"
                return
            }
            phone.isEmpty() -> {
                binding?.phoneEt?.error = "Nomor telepon tidak boleh kosong"
                return
            }
            selfPhopto == null -> {
                Toast.makeText(this, "Foto formal tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
            ktp == null -> {
                Toast.makeText(this, "Foto KTP tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return
            }
        }

        binding?.progressBar?.visibility = View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val data = hashMapOf(
            "name" to name,
            "phone" to phone,
            "selfPhoto" to selfPhopto,
            "ktp" to ktp,
            "uid" to uid,
            "status" to "waiting",
        )

        if (uid != null) {
            Firebase
                .firestore
                .collection("sales")
                .document(uid)
                .set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        Firebase
                            .firestore
                            .collection("users")
                            .document(uid)
                            .update("role", "waiting")
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    binding?.progressBar?.visibility = View.GONE
                                    showSuccessDialog()
                                } else {
                                    binding?.progressBar?.visibility = View.GONE
                                    showFailureDialog()
                                }
                            }
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        showFailureDialog()
                    }
                }
        }

    }

    private fun showSuccessDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Registrasi Sales Berhasil")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("Anda berhasil terdaftar sebagai sales pada aplikasi\n\nSilahkan tunggu beberapa saat, admin Y Found akan memverifikasi data anda, dan sesaat setelahnya akan dapat melakukan order")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            startActivity(Intent(this, HomepageActivity::class.java))
        }
        alertDialog.show()
    }

    private fun showFailureDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Registrasi Sales Tidak Berhasil")
        alertDialog.setIcon(R.drawable.ic_baseline_clear_24)
        alertDialog.setMessage("Anda tidak berhasi mendaftar, silahkan periksa data yang anda inputkan, beserta koneksi internet\n\nSilahkan coba beberapa saat lagi")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            startActivity(Intent(this, HomepageActivity::class.java))
        }
        alertDialog.show()
    }

    private fun inputKtp() {
        ImagePicker.with(this)
            .galleryOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(ktpCode)
    }

    private fun inputFormalPhoto() {
        ImagePicker.with(this)
            .galleryOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(selfPhotoCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == selfPhotoCode) {
                data?.data?.let { uploadImageToDatabase(it, "selfPhoto") }

                // tampilkan gambar produk ke halaman AddProductActivity
                binding?.selfPhoto?.let {
                    Glide.with(this)
                        .load(data?.data)
                        .into(it)
                }
            } else {
                data?.data?.let { uploadImageToDatabase(it, "ktp") }

                // tampilkan gambar produk ke halaman AddProductActivity
                binding?.ktp?.let {
                    Glide.with(this)
                        .load(data?.data)
                        .into(it)
                }
            }
        }
    }

    private fun uploadImageToDatabase(data: Uri, option: String) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        binding?.progressBar?.visibility = View.VISIBLE
        val imageFileName = "sales_registration/$option" + System.currentTimeMillis() + ".png"


        mStorageRef.child(imageFileName).putFile(data)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        binding?.progressBar?.visibility = View.GONE

                        if (option == "ktp") {
                            ktp = uri.toString()
                            binding?.addHint2?.visibility = View.GONE
                        } else {
                            selfPhopto = uri.toString()
                            binding?.addHint?.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Failed added product dp",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        if (option == "ktp") {
                            binding?.addHint2?.visibility = View.VISIBLE
                        } else {
                            binding?.addHint?.visibility = View.VISIBLE
                        }
                    }
            }
            .addOnFailureListener {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Failed added product dp",
                    Toast.LENGTH_SHORT
                ).show()
                if (option == "ktp") {
                    binding?.addHint2?.visibility = View.VISIBLE
                } else {
                    binding?.addHint?.visibility = View.VISIBLE
                }
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