package com.notsatria.videoscroll.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null,
) : Parcelable {
    constructor(): this("", "", "", null)
}
