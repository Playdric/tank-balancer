package com.cedric.domain.model

enum class AircraftTank(val index: Int) {
    LEFT(0),
    RIGHT(1);

    companion object {
        fun fromId(id: Int, default: AircraftTank = LEFT): AircraftTank {
            return entries.firstOrNull { it.index == id } ?: default
        }
    }
}
