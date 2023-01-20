package org.romancha.autofon.api.dto

data class State(
    val id: Long,

    val lastUpdate: Long,
    val latitude: Double,
    val longitude: Double,
    val temp: Int,
    val batteryVoltage: Int,
    val batteryConsumption: Int,
    val gpsPrecision: Double,
    val simCards: List<SimCard>

) : Comparable<State> {

    override fun compareTo(other: State): Int {
        return when {
            this.lastUpdate > other.lastUpdate -> 1
            this.lastUpdate < other.lastUpdate -> -1
            else -> 0
        }
    }

}


