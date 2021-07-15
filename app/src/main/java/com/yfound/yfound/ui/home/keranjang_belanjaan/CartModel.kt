package com.yfound.yfound.ui.home.keranjang_belanjaan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class CartModel(
    var productId: String? = null,
    var name: String? = null,
    var dp: String? = null,
    var quantity: String? = null,
    var buyerId: String? = null,
    var cartId: String? = null,
)
data class CartModel2(
    var name: String? = null,
    var quantity: String? = null
)