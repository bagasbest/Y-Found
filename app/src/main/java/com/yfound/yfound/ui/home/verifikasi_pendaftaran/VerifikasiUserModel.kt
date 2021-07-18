package com.yfound.yfound.ui.home.verifikasi_pendaftaran

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifikasiUserModel (

    var name: String? = null,
    var email: String? = null,
    var uid: String? = null,
    var status: String? = null,
    var password: String? = null,

) : Parcelable