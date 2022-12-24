package org.romancha.autofon.commons

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.KeyboardMarkup

abstract class ReplyMessage(
    private val message: suspend () -> String,
    private val replyMarkup: KeyboardMarkup? = null,
    private val actionAfterReply: suspend (bot: TelegramBot) -> Unit
) {

    suspend fun reply(bot: TelegramBot, to: dev.inmo.tgbotapi.types.message.abstracts.Message) {
        val msg = message()

        bot.reply(
            to,
            replyMarkup = replyMarkup,
        ) {
            +msg
        }
        actionAfterReply(bot)
    }

}

object ChartsReply : ReplyMessage(
    message = { "Генерирую графики..." },
    actionAfterReply = { bot ->
        bot.sendCharts()
    }
)

object StatesSummaryReply : ReplyMessage(
    message = { "Получаю данные..." },
    actionAfterReply = { bot ->
        VehicleStateDataSummaryMessage.send(bot)
    }
)

object SubscribeSettingsReply : ReplyMessage(
    message = {
        getCurrentListenVehicleNamesMessage("Хочешь изменить отслеживание устройств?")
    },

    replyMarkup = inlineKeyboard {
        subscribeSettingsMenu()
    },
    actionAfterReply = { bot ->
        SubscribeSettingsMessage.send(bot)
    }
)
