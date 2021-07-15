package com.yfound.yfound.ui.home.verifikasi_sales

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.LoginActivity
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityVerifikasiDetailBinding

class VerifikasiDetailActivity : AppCompatActivity() {
    private var binding: ActivityVerifikasiDetailBinding? = null
    private var uid: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Data Sales"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = intent.getParcelableExtra<VerifikasiModel>(EXTRA_SALES) as VerifikasiModel

        binding?.roundedImageView2?.let {
            Glide
                .with(this)
                .load(data.selfPhoto)
                .into(it)
        }

        binding?.ktp?.let {
            Glide
                .with(this)
                .load(data.ktp)
                .into(it)
        }

        binding?.name?.text = data.name
        binding?.phone?.text = "Nomor telepon: " + data.phone
        uid = data.uid

        //CLICK VERIFICATION
        clickVerification(data.name)

        // CLICK TOLAK
        clickDeclineVerification(data.name)

    }

    private fun clickDeclineVerification(name: String?) {
        binding?.noBtn?.setOnClickListener {
            val dialog = this.let { it1 -> AlertDialog.Builder(it1) }
            dialog.setTitle("Konfirmasi Tolak Pendaftaran Sales")
            dialog.setMessage("Apakah anda yakin ingin menolak pendaftaran: $name ?")
            dialog.setIcon(R.drawable.ic_baseline_warning_24)
            dialog.setPositiveButton("YA"){ it2,_ ->
                it2.dismiss()
                binding?.progressBar?.visibility = View.VISIBLE
                uid?.let {
                    Firebase
                        .firestore
                        .collection("sales")
                        .document(it)
                        .delete()
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {
                                binding?.progressBar?.visibility = View.GONE
                                Toast.makeText(this, "Berhasil menghapus data $name", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                binding?.progressBar?.visibility = View.GONE
                                Toast.makeText(this, "Gagal menghapus data $name", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            dialog.setNegativeButton("Tidak") { dialogs, _ ->
                dialogs.dismiss()
            }
            dialog.show()
        }
    }

    private fun clickVerification(name: String?) {
        binding?.yesBtn?.setOnClickListener {
            showConfirmDialog(name)
        }
    }

    private fun showConfirmDialog(name: String?) {
        val dialog = this.let { it1 -> AlertDialog.Builder(it1) }
        dialog.setTitle("Konfirmasi Verifikasi Sales")
        dialog.setMessage("Apakah anda yakin ingin memverifikasi sales dengan nama: $name ?")
        dialog.setIcon(R.drawable.ic_baseline_warning_24)
        dialog.setPositiveButton("YA"){ it2,_ ->
            it2.dismiss()
            binding?.progressBar?.visibility = View.VISIBLE
            uid?.let {
                Firebase
                    .firestore
                    .collection("users")
                    .document(it)
                    .update("role", "sales")
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Berhasil memverifikasi $name", Toast.LENGTH_SHORT).show()

                            binding?.yesBtn?.visibility = View.INVISIBLE
                            binding?.noBtn?.visibility = View.INVISIBLE

                            Firebase
                                .firestore
                                .collection("sales")
                                .document(uid!!)
                                .delete()
                        }
                        else {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(this, "Gagal memverifikasi $name", Toast.LENGTH_SHORT).show()
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

    companion object {
        const val EXTRA_SALES = "sales"
    }
}