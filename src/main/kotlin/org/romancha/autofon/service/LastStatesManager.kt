package org.romancha.autofon.service

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonArray
import mu.KotlinLogging
import org.romancha.autofon.api.dto.State
import org.romancha.autofon.api.dto.StateOriginal
import org.romancha.autofon.api.dto.Vehicle
import org.romancha.autofon.api.rest.AutofonController
import org.romancha.autofon.format

private val log = KotlinLogging.logger {}

object LastStatesManager {

    suspend fun getAllStates(): Map<Vehicle, List<State>> {
        return VehiclesListener.list().associateWith {
            Repo.getStatesForVehicleId(it.id)
        }
    }

    fun getLastStateForVehicle(vehicleId: Long): State? {
        val states = Repo.getStatesForVehicleId(vehicleId)
        if (states.isEmpty()) {
            return null
        }

        return states.last()
    }

    suspend fun syncLastStates() {
        log.debug { "Syncing last states" }

        val lastStatesData = AutofonController.lastStates()
        if (lastStatesData.isBlank()) {
            log.debug { "Last states data is empty" }
            return
        }

        for (node in format.decodeFromString<JsonArray>(lastStatesData)) {
            val (id, lastUpdate) = format.decodeFromJsonElement(StateOriginal.serializer(), node)

            val localLastState = getLastStateForVehicle(id)
            if (localLastState == null || localLastState.lastUpdate < lastUpdate) {
                val newStateJson = format.encodeToString(node)
                log.debug { "Adding new state for device $id with data $newStateJson" }
                Repo.saveState(id, newStateJson)
            }
        }
    }

}