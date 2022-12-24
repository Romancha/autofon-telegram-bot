package org.romancha.autofon.service

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging
import org.mapdb.DB
import org.mapdb.DBMaker
import org.mapdb.Serializer
import org.romancha.autofon.BotProps
import org.romancha.autofon.api.dto.State
import org.romancha.autofon.api.dto.Vehicle
import org.romancha.autofon.format

object Repo {

    private val log = KotlinLogging.logger {}

    private const val LISTEN_VEHICLES_DB = "listen_vehicles"
    private const val VEHICLES_DB = "vehicles"

    fun saveState(id: Long, state: String) {
        doOnDb { db ->
            val statesRepo = db.indexTreeList("states_${id}", Serializer.STRING).createOrOpen()

            statesRepo.add(state)
        }
    }

    fun getStatesForVehicleId(vehicleId: Long): List<State> {
        return doOnDb { db ->
            val states = db.indexTreeList("states_$vehicleId", Serializer.STRING).createOrOpen()

            states.toList().filterNotNull().map {
                format.decodeFromString(it)
            }
        }
    }

    fun saveVehicles(vehicles: List<Vehicle>) {
        return doOnDb { db ->
            val listenVehicles = db.hashMap(VEHICLES_DB, Serializer.LONG, Serializer.STRING).createOrOpen()

            val data = vehicles.map { it.id to format.encodeToString(it) }

            log.debug { "Save vehicles: $data" }

            listenVehicles.putAll(data)
        }
    }

    fun getVehicles(): List<Vehicle> {
        return doOnDb { db ->
            val listenVehicles = db.hashMap(VEHICLES_DB, Serializer.LONG, Serializer.STRING).createOrOpen()

            listenVehicles.values.map {
                format.decodeFromString(it!!)
            }
        }
    }

    fun getListenVehiclesId(): List<Long> {
        return doOnDb { db ->
            val states = db.indexTreeList(LISTEN_VEHICLES_DB, Serializer.LONG).createOrOpen()

            states.toList().filterNotNull()
        }
    }

    fun addListenVehicle(vehicleId: Long) {
        doOnDb { db ->
            val states = db.indexTreeList(LISTEN_VEHICLES_DB, Serializer.LONG).createOrOpen()

            states.add(vehicleId)
        }
    }

    fun removeListenVehicle(vehicleId: Long) {
        doOnDb { db ->
            val states = db.indexTreeList(LISTEN_VEHICLES_DB, Serializer.LONG).createOrOpen()

            states.remove(vehicleId)
        }
    }

    private inline fun <T> doOnDb(action: (db: DB) -> T): T {
        return DBMaker.fileDB(BotProps.dbFile)
            .fileMmapEnable()
            .make()
            .use { db ->
                action(db)
            }

    }
}