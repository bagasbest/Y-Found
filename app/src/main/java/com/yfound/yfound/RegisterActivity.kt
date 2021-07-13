package com.yfound.yfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.yfound.yfound.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Registrasi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // SUDAH PUNYA AKUN
        binding?.registerTv?.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        // MENDAFTAR
        binding?.registerBtn?.setOnClickListener {
            validateFormRegistration()
        }


    }

    private fun validateFormRegistration() {
        val email = binding?.emailEt?.text.toString().trim()
        val password = binding?.passwordEt?.text.toString().trim()
        val name = binding?.nameEt?.text.toString().trim()

        if (email.isEmpty()) {
            binding?.emailEt?.error = "Email tidak boleh kosong"
            return
        }
        if (password.isEmpty()) {
            binding?.passwordEt?.error = "Kata sandi tidak boleh kosong"
            return
        }
        if (name.isEmpty()) {
            binding?.nameEt?.error = "Nama Lengkap tidak boleh kosong"
            return
        }

        // MASUKKAN EMAIL & PASSWORD KE DALAM FIREBASE AUTH
        registerUserToDb(email, password, name)

    }

    private fun registerUserToDb(email: String, password: String, name: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        FirebaseAuth
            .getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // SIMPAN DATA PENGGUNA KEDALAM FIREBASE FIRESTORE (DATABASE)
                    saveUserToDB(name, email)
                } else {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Gagal mendaftar: silahkan cek data diri yang anda masukkan, serta cek koneksi internet anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToDB(name: String, email: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val data = hashMapOf(
            "name" to name,
            "email" to email,
            "uid" to uid,
        )

        if (uid != null) {
            Firebase
                .firestore
                .collection("users")
                .document(uid)
                .set(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(this, "Selamat, Anda berhasil terdaftar", Toast.LENGTH_SHORT)
                            .show()

                        // TAMPILKAN ALERT DIALOG SUKSES
                        showAlertDialogSuccessRegister()

                    } else {
                        binding?.progressBar?.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Gagal mendaftar: silahkan cek data diri yang anda masukkan, serta cek koneksi internet anda",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun showAlertDialogSuccessRegister() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Registrasi Berhasil")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("Anda berhasil terdaftar pada aplikasi Y Found")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(this, HomepageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        alertDialog.show()
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