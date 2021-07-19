package com.yfound.yfound.ui.pengiriman_barang

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeliveryModel(
    var deliveryId: String? = null,
    var deliveryDate: String? = null,
    var location: String? = null,
    var dp: String? = null,
    var locationQuery: String? = null,
) : Parcelable