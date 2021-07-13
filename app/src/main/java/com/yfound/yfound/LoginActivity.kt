package com.yfound.yfound

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yfound.yfound.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Halaman Login"

        // OTOMATIS LOGIN JIKA SEBELUMNYA PERNAH LOGIN
        autoLogin()

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
            startActivity(Intent(this, HomepageActivity::class.java))
            finish()
        }
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
                    val intent = Intent(this, HomepageActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
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