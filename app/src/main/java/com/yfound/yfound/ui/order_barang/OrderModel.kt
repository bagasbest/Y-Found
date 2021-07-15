package com.yfound.yfound.ui.order_barang

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderModel(
    var buyerId: String? = null,
    var buyerName: String? = null,
    var cart: List<OrderCartModel>? = null,
    var orderDate: String? = null,
    var orderId: String? = null,
    var location: String? = null,
    var status: String? = null
) : Parcelable

@Parcelize
data class OrderCartModel(
    var name: String? = null,
    var quantity: String? = null,
) : Parcelable
