package com.yfound.yfound.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel : ViewModel() {
    private val productList = MutableLiveData<ArrayList<HomeModel>>()
    private val TAG = HomeViewModel::class.java.simpleName
    private val listItem = ArrayList<HomeModel>()

    fun setAllProduct() {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("product")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = HomeModel()
                        val name = document.data["name"].toString()
                        model.name = name.toCapitalizeWord()
                        model.dp = document.data["dp"].toString()
                        model.productId = document.data["productId"].toString()
                        model.addedDate = document.data["addedDate"].toString()

                        listItem.add(model)
                    }
                    productList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setProductByQuery(query: String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("product")
                .whereGreaterThanOrEqualTo("name", query)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = HomeModel()
                        val name = document.data["name"].toString()
                        model.name = name.toCapitalizeWord()
                        model.dp = document.data["dp"].toString()
                        model.productId = document.data["productId"].toString()
                        model.addedDate = document.data["addedDate"].toString()

                        listItem.add(model)
                    }
                    productList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }


    fun getAllProduct() : LiveData<ArrayList<HomeModel>> {
        return productList
    }

    @SuppressLint("DefaultLocale")
    private fun String.toCapitalizeWord(): String = split(" ").joinToString(" ") { it.replaceFirstChar { data ->
        if (data.isLowerCase()) data.titlecase(
            Locale.getDefault()
        ) else data.toString()
    } }
}

