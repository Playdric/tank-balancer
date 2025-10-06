package com.cedric.tankbalancer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AircraftTank(val index: Int) : Parcelable {
    LEFT(0),
    RIGHT(1);

    companion object {
        fun fromId(id: Int, default: AircraftTank = LEFT): AircraftTank {
            return entries.firstOrNull { it.index == id } ?: default
        }
    }
}
