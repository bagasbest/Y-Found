package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CartViewModel : ViewModel() {
    private val cartList = MutableLiveData<ArrayList<CartModel>>()
    private val TAG = CartModel::class.java.simpleName
    val listItem = ArrayList<CartModel>()

    fun setCartByBuyerId(buyerId:String) {
        listItem.clear()

        try {
            Firebase
                .firestore
                .collection("cart")
                .whereEqualTo("buyerId", buyerId)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        val model = CartModel()
                        model.productId = document.data["productId"].toString()
                        model.name = document.data["name"].toString()
                        model.dp = document.data["dp"].toString()
                        model.quantity = document.data["quantity"].toString()
                        model.buyerId = document.data["buyerId"].toString()
                        model.cartId = document.data["cartId"].toString()

                        listItem.add(model)
                    }
                    cartList.postValue(listItem)
                }
                .addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getAllCart() : LiveData<ArrayList<CartModel>> {
        return cartList
    }
}