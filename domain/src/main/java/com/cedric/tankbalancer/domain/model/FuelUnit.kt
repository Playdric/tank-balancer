package com.cedric.tankbalancer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FuelUnit : Parcelable {
    METRIC,
    IMPERIAL
}
