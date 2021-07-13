package com.yfound.yfound.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeModel(
    var name: String? = null,
    var dp: String? = null,
    var productId: String? = null,
    var addedDate: String? = null,
) : Parcelable