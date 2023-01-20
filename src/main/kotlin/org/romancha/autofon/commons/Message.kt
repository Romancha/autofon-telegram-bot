package org.romancha.autofon.commons

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.utils.types.buttons.replyKeyboard
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup
import dev.inmo.tgbotapi.utils.row
import org.romancha.autofon.BotProps
import org.romancha.autofon.service.LastStatesManager

abstract class Message(
    private val message: suspend () -> String,
    private val replyMarkup: KeyboardMarkup? = null,
    private val silent: Boolean = false
) {

    suspend fun send(bot: TelegramBot, chatId: ChatIdentifier = ChatId(BotProps.chaId)) {
        bot.sendMessage(
            chatId = chatId,
            text = message(),
            replyMarkup = replyMarkup,
            disableNotification = silent
        )
    }

}

object StartMessage : Message(
    message = {
        """
        🖖 Привет, это бот AutoFon.
        Я слежу за состоянием ваших устройств 🚗 и отправляю уведомления при обновлении данных.
        А еще я умею рисовать исторические графики 📈 и предупреждать о маленьком балансе 💵 на sim-карте.
        
        Я open source и self-hosted, исходный код доступен по ссылке: https://github.com/Romancha/autofon-telegram-bot
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