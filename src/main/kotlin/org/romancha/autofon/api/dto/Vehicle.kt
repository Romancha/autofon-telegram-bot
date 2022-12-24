package org.romancha.autofon.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Vehicle(
    val id: Long,

    @SerialName("desc")
    val name: String,

    val type: Int,
) {

    companion object {
        private const val TYPE = 1

        val filter = { vehicle: Vehicle -> vehicle.type == TYPE }
    }

}