package com.yfound.yfound.ui.order_barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class OrderViewModel : ViewModel() {
    private val orderList = MutableLiveData<ArrayList<OrderModel>>()
    private val TAG = OrderModel::class.java.simpleName
    private val listItem = ArrayList<OrderModel>()

    fun setAllOrderByStatus(status: String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("order")
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = OrderModel()
                        model.buyerId = document.data["buyerId"].toString()
                        model.buyerName = document.data["buyerName"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.orderDate = document.data["orderDate"].toString()
                        model.buyerId = document.data["buyerId"].toString()
                        model.location = document.data["location"].toString()
                        model.status = document.data["status"].toString()
                        model.cart = document.toObject(OrderModel::class.java).cart

                        listItem.add(model)
                    }
                    orderList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun setSearchedByStatus(status: String, query: String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("order")
                .whereEqualTo("status", status)
                .whereArrayContains("productName", query)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = OrderModel()
                        model.buyerId = document.data["buyerId"].toString()
                        model.buyerName = document.data["buyerName"].toString()
                        model.orderId = document.data["orderId"].toString()
                        model.orderDate = document.data["orderDate"].toString()
                        model.buyerId = document.data["buyerId"].toString()
                        model.location = document.data["location"].toString()
                        model.status = document.data["status"].toString()
                        model.cart = document.toObject(OrderModel::class.java).cart

                        listItem.add(model)
                    }
                    orderList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getAllOrder() : LiveData<ArrayList<OrderModel>> {
        return orderList
    }

}