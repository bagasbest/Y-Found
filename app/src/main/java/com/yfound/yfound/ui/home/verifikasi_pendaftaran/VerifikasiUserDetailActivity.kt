package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.R
import com.yfound.yfound.databinding.ActivityVerifikasiUserDetailBinding
import java.nio.charset.StandardCharsets


class VerifikasiUserDetailActivity : AppCompatActivity() {

    var binding: ActivityVerifikasiUserDetailBinding? = null
    private lateinit var data: VerifikasiUserModel
    private var adminPassword: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifikasiUserDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Verifikasi Akun Penguna"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        data = intent.getParcelableExtra<VerifikasiUserModel>(EXTRA_USER) as VerifikasiUserModel
        binding?.name?.text = "Nama: " + data.name
        binding?.email?.text = "Email: " + data.email
        binding?.status?.text = "Status Akun: " + data.status

        if (intent.getStringExtra(EXTRA_STATUS) == "waiting") {
            binding?.accept?.visibility = View.VISIBLE
            binding?.decline?.visibility = View.VISIBLE
        }

        binding?.accept?.setOnClickListener {
            acceptUser()
        }

        binding?.decline?.setOnClickListener {
            declineUser()
        }
    }

    private fun acceptUser() {
        showConfirmDialog()
    }

    private fun showConfirmDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Konfirmasi Aktivasi Akun")
        alertDialog.setIcon(R.drawable.ic_baseline_warning_24)
        alertDialog.setMessage("Apakah anda yakin ingin mengkatifkan akun: ${data.name} ?")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            binding?.progressBar?.visibility = View.VISIBLE
            data.uid?.let {
                Firebase
                    .firestore
                    .collection("users")
                    .document(it)
                    .update("status", "active")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Akun ${data.name} berhasil diaktifkan",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding?.accept?.visibility = View.GONE
                            binding?.decline?.visibility = View.GONE
                        } else {
                            binding?.progressBar?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Akun ${data.name} berhasil diaktifkan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun declineUser() {
        showDeclineDialog()
    }

    private fun showDeclineDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Konfirmasi Menolak Akun")
        alertDialog.setIcon(R.drawable.ic_baseline_warning_24)
        alertDialog.setMessage("Apakah anda yakin ingin menolak akun: ${data.name} ?\n\nAkun ${data.name} akan dihapus dari database")
        alertDialog.setPositiveButton("Tetap Tolak") { dialog, _ ->
            dialog.dismiss()
            deleteAccount()
        }
        alertDialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun deleteAccount() {
        binding?.progressBar?.visibility = View.VISIBLE
        val user = FirebaseAuth.getInstance()
        val adminEmail = FirebaseAuth.getInstance().currentUser?.email
        user.currentUser?.uid?.let {
            Firebase
                .firestore
                .collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    adminPassword = fromBase64(doc["password"].toString())
                }
        }

        user.signOut()
        user.signInWithEmailAndPassword(data.email!!, fromBase64(data.password))
            .addOnCompleteListener { data3 ->

                if (data3.isSuccessful) {
                    FirebaseAuth
                        .getInstance()
                        .currentUser
                        ?.delete()
                        ?.addOnCompleteListener { task ->


                            if (task.isSuccessful) {
                                FirebaseAuth
                                    .getInstance()
                                    .signInWithEmailAndPassword(adminEmail!!, adminPassword!!)
                                    .addOnCompleteListener { task2 ->

                                        if (task2.isSuccessful) {
                                            deleteDataUser()
                                        } else {
                                            binding?.progressBar?.visibility = View.GONE
                                            Toast.makeText(
                                                this,
                                                "Gagal menghapus akun",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                    }
                            } else {
                                binding?.progressBar?.visibility = View.GONE
                                Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT)
                                    .show()
                            }


                        }
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteDataUser() {
        data.uid?.let {
            Firebase
                .firestore
                .collection("users")
                .document(it)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(this, "Berhasil menghapus akun", Toast.LENGTH_SHORT).show()
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun fromBase64(password: String?): String {
        return String(
            android.util.Base64.decode(password, android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
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
        const val EXTRA_STATUS = "status"
        const val EXTRA_USER = "user"
    }

}