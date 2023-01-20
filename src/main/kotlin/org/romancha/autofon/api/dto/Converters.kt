package org.romancha.autofon.api.dto

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.Option
import kotlinx.serialization.decodeFromString

fun String.jsonToState(): State {
    val stateOriginal: StateOriginal = org.romancha.autofon.format.decodeFromString(this)

    val conf = Configuration.defaultConfiguration()
    conf.addOptions(Option.ALWAYS_RETURN_LIST)

    val balances: List<Double> = JsonPath.read(this, "$..Balance")

    return State(
        id = stateOriginal.id,
        lastUpdate = stateOriginal.lastUpdate,
        latitude = stateOriginal.latitude,
        longitude = stateOriginal.longitude,
        temp = stateOriginal.temp,
        batteryVoltage = stateOriginal.batteryVoltage,
        batteryConsumption = stateOriginal.batteryConsumption,
        gpsPrecision = stateOriginal.gpsPrecision,
        simCards = balances.map { SimCard(it) }
    )
}