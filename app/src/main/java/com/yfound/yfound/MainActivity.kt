package com.yfound.yfound

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadSplashScreen()

    }

    private fun loadSplashScreen() {
        Looper.myLooper()?.let {
            Handler(it).postDelayed({

                checkIfUserAlreadyLoginBefore()

            }, 3000)
        }
    }

    private fun checkIfUserAlreadyLoginBefore() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            if (uid != null) {
                Firebase
                    .firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener {
                        if (it["status"].toString() == "active") {
                            val intent = Intent(this, HomepageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            val mainIntent = Intent(this, LoginActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }
                    }
            }
        } else {
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }
}