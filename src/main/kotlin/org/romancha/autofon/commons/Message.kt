package org.romancha.autofon.commons

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import org.romancha.autofon.BotProps
import org.romancha.autofon.service.LastStatesManager

abstract class Message(
    private val message: suspend () -> String,
    private val replyMarkup: KeyboardMarkup? = null,
    private val silent: Boolean = false
) {

    suspend fun send(bot: TelegramBot) {
        bot.sendMessage(
            chatId = ChatId(BotProps.chaId),
            text = message(),
            replyMarkup = replyMarkup,
            disableNotification = silent
        )
    }

}

object StartMessage : Message(
    message = {
        """
        🖖 Привет, я бот AutoFon.
        Я слежу за состоянием ваших устройств 🚗 и отправляю уведомления при обновлении данных.
        А еще я умею рисовать исторические графики 📈.
        """.trimIndent()
    },
    replyMarkup = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = false) {
        mainMenu()
    }
)

object BackMessage : Message(
    message = {
        "Привет! Чем могу помочь?"
    },
    replyMarkup = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = false) {
        mainMenu()
    }
)

object SubscribeSettingsMessage : Message(
    message = {
        "Настройка отслеживания"
    },
    replyMarkup = replyKeyboard(resizeKeyboard = true, oneTimeKeyboard = false) {
        row {
            simpleButton(Actions.BACK.title)
        }
    }
)

object HeartbeatMessage : Message(
    message = {
        "Я жив!"
    },
    silent = true
)

object VehicleStateDataSummaryMessage : Message(
    message = {
        val allStates = LastStatesManager.getAllStates()
        val statesSummary = if (allStates.isNotEmpty()) {
            allStates.map {
                it.key.name + ": " + it.value.size
            }.joinToString("\n")
        } else {
            "Нет данных"
        }

        "Сохраненные  состояния по устройствам:\n$statesSummary"
    }
)