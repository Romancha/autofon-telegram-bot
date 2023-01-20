package org.romancha.autofon.service

import dev.inmo.micro_utils.common.MPPFile
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import mu.KotlinLogging
import org.jetbrains.letsPlot.Stat
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomHistogram
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleFillGradient2
import org.jetbrains.letsPlot.scale.scaleXDateTime
import org.romancha.autofon.BotProps
import org.romancha.autofon.api.dto.State
import org.romancha.autofon.api.dto.Vehicle
import java.time.ZonedDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

private const val TEMP_DATA = "temp"
private const val TIME_DATA = "date"
private const val VOLTAGE_DATA = "voltage"

object Charts {

    fun allCharts(vehicle: Vehicle): List<InputFile> {
        val states = Repo.getStatesForVehicleId(vehicle.id)
        if (states.isEmpty()) {
            return emptyList()
        }

        return listOfNotNull(
            temperatureBar(vehicle, states),
            batteryVoltageLine(vehicle, states),
            batteryConsumptionLine(vehicle, states)
        )
    }

    private fun temperatureBar(vehicle: Vehicle, stateOriginals: List<State>): InputFile? {
        if (stateOriginals.isEmpty()) {
            return null
        }

        val temps = mutableListOf<Int>()
        val time = mutableListOf<ZonedDateTime>()

        stateOriginals.forEach {
            temps.add(it.temp)

            val unixTime = it.lastUpdate * 1000L
            val dateTime = ZonedDateTime.ofInstant(Date(unixTime).toInstant(), BotProps.timeZone)

            time.add(dateTime)

            logger.debug { "Temp: ${it.temp}, time: $dateTime" }
        }

        val data = mapOf<String, Any>(
            TIME_DATA to time,
            TEMP_DATA to temps
        )
        val plot = letsPlot(
            data
        ) + labs(
            title = vehicle.name,
            x = "Дата",
            y = "°C"
        ) + geomHistogram(
            stat = Stat.identity,
            color = "black",
        ) {
            x = TIME_DATA
            y = TEMP_DATA
            fill = TEMP_DATA
        } + scaleXDateTime(
            format = "%b %e"
        ) + scaleFillGradient2(
            low = "blue",
            high = "red",
            mid = "white"
        )

        val barImage = ggsave(plot, "temp_histogram.png")

        return InputFile(MPPFile(barImage))
    }

    private fun batteryVoltageLine(vehicle: Vehicle, stateOriginals: List<State>): InputFile? {
        if (stateOriginals.isEmpty()) {
            return null
        }

        val voltage = mutableListOf<Double>()
        val time = mutableListOf<ZonedDateTime>()

        stateOriginals.forEach {
            voltage.add(it.batteryVoltage.toDouble() / 1000)

            val unixTime = it.lastUpdate * 1000L
            val dateTime = ZonedDateTime.ofInstant(Date(unixTime).toInstant(), BotProps.timeZone)

            time.add(dateTime)
        }

        val data = mapOf<String, Any>(
            TIME_DATA to time,
            VOLTAGE_DATA to voltage
        )

        val plot = letsPlot(
            data
        ) {
            x = TIME_DATA
            y = VOLTAGE_DATA
        } + labs(
            title = vehicle.name,
            x = "Дата",
            y = "Volt"
        ) + geomLine() + scaleXDateTime(
            format = "%b %e"
        )

        val barImage = ggsave(plot, "voltage_line.png")

        return InputFile(MPPFile(barImage))
    }

    private fun batteryConsumptionLine(vehicle: Vehicle, stateOriginals: List<State>): InputFile? {
        if (stateOriginals.isEmpty()) {
            return null
        }

        val consumption = mutableListOf<Int>()
        val time = mutableListOf<ZonedDateTime>()

        stateOriginals.forEach {
            consumption.add(it.batteryConsumption / 1000)

            val unixTime = it.lastUpdate * 1000L
            val dateTime = ZonedDateTime.ofInstant(Date(unixTime).toInstant(), BotProps.timeZone)

            time.add(dateTime)
        }

        val data = mapOf<String, Any>(
            TIME_DATA to time,
            VOLTAGE_DATA to consumption
        )

        val plot = letsPlot(
            data
        ) {
            x = TIME_DATA
            y = VOLTAGE_DATA
        } + labs(
            title = vehicle.name,
            x = "Дата",
            y = "мАч"
        ) + geomLine() + scaleXDateTime(
            format = "%b %e"
        )

        val barImage = ggsave(plot, "consumption_line.png")

        return InputFile(MPPFile(barImage))
    }

}