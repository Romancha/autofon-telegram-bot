package org.romancha.autofon.commons

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.media.sendDocumentsGroup
import dev.inmo.tgbotapi.extensions.api.send.sendLocation
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.location.StaticLocation
import dev.inmo.tgbotapi.types.media.TelegramMediaDocument
import mu.KotlinLogging
import org.romancha.autofon.BotProps
import org.romancha.autofon.service.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val logger = KotlinLogging.logger {}

suspend fun getCurrentListenVehicleNamesMessage(title: String): String {
    val currentListenVehicleNames = VehiclesListener.list().map { it.name }
    var msg = "$title\n"
    msg += if (currentListenVehicleNames.isEmpty()) {
        "Сейчас я не отслеживаю ни одного устройства."
    } else {
        "Сейчас я отслеживаю устройства:\n ${currentListenVehicleNames.joinToString("\n")}"
    }
    return msg
}

suspend fun TelegramBot.sendCharts() {
    val listenVehicles = VehiclesListener.list()
    if (listenVehicles.isEmpty()) {
        sendMessage(
            ChatId(BotProps.chaId),
            "Я не отслеживаю ни одного устройства. Добавьте устройства в настройках."
        )
        return
    }

    listenVehicles.forEach { vehicle ->
        val tempChart = Charts.allCharts(vehicle)

        if (tempChart.isNotEmpty()) {
            sendDocumentsGroup(
                chatId = ChatId(BotProps.chaId),
                tempChart.map {
                    TelegramMediaDocument(it)
                }
            )
        } else {
            sendMessage(
                chatId = ChatId(BotProps.chaId),
                text = "Нет данных для отображения графиков"
            )
        }
    }
}

suspend fun TelegramBot.sendNewVehicleStatus(lastUpdateState: MutableMap<Long, Long>) {
    LastStatesManager.syncLastStates()

    VehiclesListener.list().forEach { vehicle ->
        logger.info { "Check notifiable device state for $vehicle" }
        val id = vehicle.id

        val state = LastStatesManager.getLastStateForVehicle(id)

        state?.let {
            if (it.lastUpdate > lastUpdateState.getOrDefault(id, 0L)) {
                logger.info { "New state for ${vehicle.name}" }
                lastUpdateState[it.id] = it.lastUpdate

                val location = StaticLocation(state.longitude, state.latitude)

                val unixTime = state.lastUpdate * 1000L
                val readableTime = let {
                    val dateMoscow =
                        ZonedDateTime.ofInstant(Date(unixTime).toInstant(), BotProps.timeZone)
                    dateMoscow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z"))
                }

                val msg = "${vehicle.name} новые данные" +
                        "\n\uD83D\uDD5B $readableTime " +
                        "\n\uD83C\uDF21️ ${state.temp}" +
                        "\n\uD83D\uDD0B ${state.batteryVoltage.toDouble() / 1000} volt, потрачено ${state.batteryConsumption / 1000} мАч" +
                        "\n" +
                        "\nточность ${state.gpsPrecision} метров:"

                sendMessage(
                    chatId = ChatId(BotProps.chaId),
                    text = msg
                )

                sendLocation(
                    chatId = ChatId(BotProps.chaId),
                    location = location,
                    disableNotification = true
                )

                val oneOfSimHasCriticalBalance =
                    state.simCards.any { sim -> sim.balance < BotProps.simBalanceAlarmThreshold }
                if (oneOfSimHasCriticalBalance) {
                    sendMessage(
                        chatId = ChatId(BotProps.chaId),
                        text = "❗Внимание❗ На одной из симкарт устройства ${vehicle.name} осталось меньше " +
                                "${BotProps.simBalanceAlarmThreshold} единиц баланса.\nТекущий баланс: " +
                                state.simCards.joinToString { sim -> sim.balance.toString() }
                    )
                }
            }
        }
    }
}