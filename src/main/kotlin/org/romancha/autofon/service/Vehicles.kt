package org.romancha.autofon.service

import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import org.romancha.autofon.api.dto.Vehicle
import org.romancha.autofon.api.rest.AutofonController
import org.romancha.autofon.format
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object Vehicles {

    private val syncActive = AtomicBoolean(false)

    private val log = KotlinLogging.logger {}
    suspend fun startSync() {
        if (syncActive.get()) {
            log.warn { "Sync already active" }
            return
        }

        syncActive.set(true)

        while (true) {
            delay(TimeUnit.SECONDS.toMillis(10))

            try {
                log.info { "Syncing vehicles" }
                val vehicles = getVehiclesFromApi()
                if (vehicles.isEmpty()) {
                    return
                }

                Repo.saveVehicles(vehicles)

                log.info { "Synced vehicles" }
            } catch (e: Exception) {
                log.error(e) { "Error syncing vehicles" }
            }

            delay(TimeUnit.MINUTES.toMillis(25))
        }
    }

    private suspend fun getVehiclesFromApi(): List<Vehicle> {
        val vehicles = AutofonController.objects()
        if (vehicles.isBlank()) {
            log.warn { "Vehicles data from api is empty" }
            return emptyList()
        }

        return format.decodeFromString<List<Vehicle>>(vehicles).filter { Vehicle.filter(it) }
    }

    suspend fun getVehicles(): List<Vehicle> {
        val vehiclesFromRepo = Repo.getVehicles()
        return vehiclesFromRepo.ifEmpty {
            getVehiclesFromApi()
        }
    }

}