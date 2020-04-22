package com.abdhilabs.learn.workshopdscyk.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Rating(
    var userId: String? = null,
    var userName: String? = null,
    var rating: Double? = null,
    var text: String? = null,
    @ServerTimestamp var timestamp: Date? = null
) : Parcelable