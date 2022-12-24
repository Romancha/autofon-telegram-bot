package org.romancha.autofon.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class State(
    val id: Long,

    @SerialName("tscrd")
    val lastUpdate: Long,

    @SerialName("lat")
    val latitude: Double,

    @SerialName("lng")
    val longitude: Double,

    @SerialName("tmp")
    val temp: Int,

    @SerialName("ipwr")
    val batteryVoltage: Int,

    @SerialName("consumption")
    val batteryConsumption: Int,

    @SerialName("precision")
    val gpsPrecision: Double
) : Comparable<State> {

    override fun compareTo(other: State): Int {
        return when {
            this.lastUpdate > other.lastUpdate -> 1
            this.lastUpdate < other.lastUpdate -> -1
            else -> 0
        }
    }

}
