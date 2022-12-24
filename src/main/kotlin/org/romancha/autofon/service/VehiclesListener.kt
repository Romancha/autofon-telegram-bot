package org.romancha.autofon.service

import org.romancha.autofon.api.dto.Vehicle

const val ADD_VEHICLE_PREFIX = "listen_add_vehicleId:"
const val REMOVE_VEHICLE_PREFIX = "listen_remove_vehicleId:"

object VehiclesListener {

    suspend fun list(): List<Vehicle> {
        val currentListenDevicesId = Repo.getListenVehiclesId()
        if (currentListenDevicesId.isEmpty()) {
            return emptyList()
        }

        val allDevices = Vehicles.getVehicles()

        return currentListenDevicesId.map {
            allDevices.find { device -> device.id == it } ?: Vehicle(it, "Unknown", 0)
        }
    }

    suspend fun availableToAdd(): List<Vehicle> {
        val currentListenDevicesId = Repo.getListenVehiclesId()
        val allDevices = Vehicles.getVehicles()

        return allDevices.filter { device -> !currentListenDevicesId.contains(device.id) }
    }

    suspend fun availableToRemove(): List<Vehicle> {
        val currentListenDevicesId = Repo.getListenVehiclesId()
        val allDevices = Vehicles.getVehicles()

        return allDevices.filter { device -> currentListenDevicesId.contains(device.id) }
    }

    suspend fun add(vehicleId: Long) {
        Repo.addListenVehicle(vehicleId)
    }

    suspend fun remove(vehicleId: Long) {
        Repo.removeListenVehicle(vehicleId)
    }

}