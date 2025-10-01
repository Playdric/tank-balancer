package com.cedric.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FuelUnit : Parcelable {
    METRIC,
    IMPERIAL
}
