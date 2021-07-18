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
import com.yfound.yfound.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Halaman Login"

        // BELUM PUNYA AKUN
        binding?.registerTv?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // LOGIN
        binding?.loginBtn?.setOnClickListener {
            validateFormLogin()
        }

    }

    private fun autoLogin() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                Firebase
                    .firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener {
                        if(it["status"].toString() == "active") {
                            val intent = Intent(this, HomepageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                        else {
                            showWaitingDialog()
                        }
                    }
            }


        }
    }

    private fun showWaitingDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Akun Sedang Diverifikasi Admin")
        alertDialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        alertDialog.setMessage("Untuk masuk kedalam aplikasi, mohon menunggu verifikasi oleh admin")
        alertDialog.setPositiveButton("OKE") { dialog, _ ->
            // sign out dari firebase autentikasi
            FirebaseAuth.getInstance().signOut()
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun validateFormLogin() {
        val email = binding?.emailEt?.text.toString().trim()
        val password = binding?.passwordEt?.text.toString().trim()

        if(email.isEmpty()) {
            binding?.emailEt?.error = "Maaf, anda harus mengisi email"
            return
        }
        if(password.isEmpty()) {
            binding?.passwordEt?.error = "Maaf, anda harus mengisi kata sandi"
            return
        }

        // LOGIN MENGGUNAKAN EMAIL DAN KATA SANDI
        loginWithEmailAndPassword(email, password)

    }

    private fun loginWithEmailAndPassword(email: String, password: String) {
        binding?.progressBar?.visibility = View.VISIBLE
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding?.progressBar?.visibility = View.GONE
                    autoLogin()
                }
                else {
                    binding?.progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Login tidak berhasil, mohon periksa email, kata sandi, dan koneksi internet anda", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}