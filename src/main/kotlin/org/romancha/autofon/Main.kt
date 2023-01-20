package org.romancha.autofon

import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.utils.withContent
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.romancha.autofon.commons.*
import org.romancha.autofon.service.ADD_VEHICLE_PREFIX
import org.romancha.autofon.service.REMOVE_VEHICLE_PREFIX
import org.romancha.autofon.service.Vehicles
import org.romancha.autofon.service.VehiclesListener
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

val format = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
}

suspend fun main() {

    val bot = telegramBot(BotProps.token)

    bot.buildBehaviourWithLongPolling(
        defaultExceptionsHandler = {
            log.error(it) { "Exception in bot" }
        }
    ) {

        launch {
            Vehicles.startSync()
        }

        StartMessage.send(bot)

        onContentMessage(
            subcontextUpdatesFilter = filterByChatIdMsg
        ) { message ->
            when (message.content) {
                is TextContent -> {
                    when ((message.content as TextContent).text) {
                        Actions.CHARTS.title -> {
                            ChartsReply.reply(bot, message)
                        }

                        Actions.STATES_SUMMARY.title -> {
                            StatesSummaryReply.reply(bot, message)
                        }

                        Actions.SUBSCRIBE_SETTINGS.title -> {
                            SubscribeSettingsReply.reply(bot, message)
                        }

                        Actions.BACK.title -> {
                            BackMessage.send(bot)
                        }
                    }
                }

                else -> {
                    reply(message, "Неизвестная команда")
                }
            }
        }

        onMessageDataCallbackQuery(
            initialFilter = filterByChaIdCallback
        ) {
            log.debug { "Callback query: $it" }

            val incoming = it.data
            when {
                incoming == Actions.SUBSCRIBE_ADD.name -> {
                    SubscribeAddEdit.edit(bot, it.message.withContent()!!)
                }

                incoming == Actions.SUBSCRIBE_REMOVE.name -> {
                    SubscribeRemoveEdit.edit(bot, it.message.withContent()!!)
                }

                incoming.startsWith(ADD_VEHICLE_PREFIX) -> {
                    val vehicleId = incoming.substring(ADD_VEHICLE_PREFIX.length).toLong()
                    VehiclesListener.add(vehicleId)

                    AddVehicleEdit.edit(bot, it.message.withContent()!!)
                }

                incoming.startsWith(REMOVE_VEHICLE_PREFIX) -> {
                    val vehicleId = incoming.substring(REMOVE_VEHICLE_PREFIX.length).toLong()
                    VehiclesListener.remove(vehicleId)

                    RemoveVehicleEdit.edit(bot, it.message.withContent()!!)
                }

                else -> {
                    log.error { "Unknown callback query data: $incoming" }
                }
            }

        }

        // heartbeat
        launch {
            if (BotProps.heartbeatEnabled) {
                while (true) {
                    delay(TimeUnit.SECONDS.toMillis(BotProps.heartbeatIntervalSeconds))
                    HeartbeatMessage.send(bot)
                }
            }
        }

        // send last update
        launch {
            val lastUpdateState = mutableMapOf<Long, Long>()

            while (true) {
                sendNewVehicleStatus(lastUpdateState)
                delay(TimeUnit.SECONDS.toMillis(BotProps.checkLastUpdateIntervalSeconds))
            }
        }

        setMyCommands(
            BotCommand("start", "Приступить к работе с ботом"),
            BotCommand("charts", "Посмотреть графики"),
            BotCommand("states_summary", "Сводка по сохраненным состояниям")
        )

        onCommand("start") {
            log.info { "Call start commandL ${it.chat.id}" }
            StartMessage.send(bot, it.chat.id)
        }

        onSecureCommand("charts") {
            bot.sendCharts()
        }

        onSecureCommand("states_summary") {
            VehicleStateDataSummaryMessage.send(bot)
        }
    }.join()

}
