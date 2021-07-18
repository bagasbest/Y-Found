package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerifikasiUserViewModel : ViewModel(){

    private val userList = MutableLiveData<ArrayList<VerifikasiUserModel>>()
    private val TAG = VerifikasiUserViewModel::class.java.simpleName
    val listItem = ArrayList<VerifikasiUserModel>()

    fun setWaitingUser() {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("users")
                .whereEqualTo("status", "waiting")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = VerifikasiUserModel()
                        model.name = document.data["name"].toString()
                        model.password = document.data["password"].toString()
                        model.email = document.data["email"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()

                        listItem.add(model)
                    }
                    userList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setActiveUser() {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("users")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = VerifikasiUserModel()
                        model.name = document.data["name"].toString()
                        model.password = document.data["password"].toString()
                        model.email = document.data["email"].toString()
                        model.status = document.data["status"].toString()
                        model.uid = document.data["uid"].toString()

                        listItem.add(model)
                    }
                    userList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getWaitingUser() : LiveData<ArrayList<VerifikasiUserModel>> {
        return userList
    }

}