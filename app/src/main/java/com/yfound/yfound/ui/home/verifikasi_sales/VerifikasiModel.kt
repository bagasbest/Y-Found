package com.yfound.yfound.ui.home.verifikasi_sales

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifikasiModel(
    var name: String? = null,
    var phone: String? = null,
    var selfPhoto: String? = null,
    var ktp: String? = null,
    var uid: String? = null,
) : Parcelable