package com.yfound.yfound.ui.home.verifikasi_sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VerifikasiViewModel : ViewModel() {

    private val salesList = MutableLiveData<ArrayList<VerifikasiModel>>()
    private val TAG = VerifikasiViewModel::class.java.simpleName
    private val listItem = ArrayList<VerifikasiModel>()

    fun setAllSales() {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("sales")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = VerifikasiModel()
                        model.name = document.data["name"].toString()
                        model.phone = document.data["phone"].toString()
                        model.selfPhoto = document.data["selfPhoto"].toString()
                        model.ktp = document.data["ktp"].toString()
                        model.uid = document.data["uid"].toString()

                        listItem.add(model)
                    }
                    salesList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getAllSales() : LiveData<ArrayList<VerifikasiModel>> {
        return salesList
    }
}