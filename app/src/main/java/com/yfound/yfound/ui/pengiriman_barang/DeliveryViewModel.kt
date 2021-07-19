package com.yfound.yfound.ui.pengiriman_barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeliveryViewModel : ViewModel() {

    private val deliveryList = MutableLiveData<ArrayList<DeliveryModel>>()
    private val TAG = DeliveryModel::class.java.simpleName
    private val listItem = ArrayList<DeliveryModel>()

    fun setAllDelivery() {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("delivery")
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = DeliveryModel()
                        model.deliveryId = document.data["deliveryId"].toString()
                        model.deliveryDate = document.data["deliveryDate"].toString()
                        model.location = document.data["location"].toString()
                        model.dp = document.data["dp"].toString()
                        model.locationQuery = document.data["locationQuery"].toString()


                        listItem.add(model)
                    }
                    deliveryList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setDeliveryByDate(date:String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("delivery")
                .whereEqualTo("deliveryDate", date)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = DeliveryModel()
                        model.deliveryId = document.data["deliveryId"].toString()
                        model.deliveryDate = document.data["deliveryDate"].toString()
                        model.location = document.data["location"].toString()
                        model.dp = document.data["dp"].toString()
                        model.locationQuery = document.data["locationQuery"].toString()


                        listItem.add(model)
                    }
                    deliveryList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setDeliveryByQuery(query:String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("delivery")
                .whereGreaterThanOrEqualTo("locationQuery", query)
                .whereLessThanOrEqualTo("locationQuery", query+ '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = DeliveryModel()
                        model.deliveryId = document.data["deliveryId"].toString()
                        model.deliveryDate = document.data["deliveryDate"].toString()
                        model.location = document.data["location"].toString()
                        model.dp = document.data["dp"].toString()
                        model.locationQuery = document.data["locationQuery"].toString()


                        listItem.add(model)
                    }
                    deliveryList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getAllDelivery() : LiveData<ArrayList<DeliveryModel>> {
        return deliveryList
    }

}