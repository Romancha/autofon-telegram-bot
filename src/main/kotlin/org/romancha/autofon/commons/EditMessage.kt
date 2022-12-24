package org.romancha.autofon.commons

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.inlineKeyboard
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.utils.row
import org.romancha.autofon.service.ADD_VEHICLE_PREFIX
import org.romancha.autofon.service.REMOVE_VEHICLE_PREFIX
import org.romancha.autofon.service.VehiclesListener

abstract class EditMessage(
    private val message: suspend () -> String,
    private val replyMarkup: suspend () -> InlineKeyboardMarkup?
) {

    suspend fun edit(bot: TelegramBot, message: ContentMessage<TextContent>) {
        val msg = message()

        bot.edit(
            message = message,
            replyMarkup = replyMarkup()
        ) {
            +msg
        }
    }

}

object SubscribeAddEdit : EditMessage(
    message = {
        val availableToAdd = VehiclesListener.availableToAdd()

        val msg = if (availableToAdd.isNotEmpty()) {
            "Выбери устройство для отслеживания."
        } else {
            "Нет доступных устройств для добавления."
        }

        getCurrentListenVehicleNamesMessage(msg)
    },
    replyMarkup = {
        val availableToAdd = VehiclesListener.availableToAdd()

        if (availableToAdd.isNotEmpty()) {
            inlineKeyboard {
                availableToAdd.forEach { vehicle ->
                    row {
                        dataButton(vehicle.name, ADD_VEHICLE_PREFIX + vehicle.id)
                    }
                }
            }
        } else {
            inlineKeyboard {
                subscribeSettingsMenu()
            }
        }
    }
)

object SubscribeRemoveEdit : EditMessage(
    message = {
        val availableToRemove = VehiclesListener.availableToRemove()

        val msg = if (availableToRemove.isNotEmpty()) {
            "Выбери устройство для исключения из отслеживания"
        } else {
            "Нет доступных устройств для исключения"
        }

        getCurrentListenVehicleNamesMessage(msg)
    },
    replyMarkup = {
        val availableToRemove = VehiclesListener.availableToRemove()

        if (availableToRemove.isNotEmpty()) {
            inlineKeyboard {
                availableToRemove.forEach { vehicle ->
                    row {
                        dataButton(vehicle.name, REMOVE_VEHICLE_PREFIX + vehicle.id)
                    }
                }
            }
        } else {
            inlineKeyboard {
                subscribeSettingsMenu()
            }
        }
    }
)

object AddVehicleEdit : EditMessage(
    message = {
        val currentListenVehicleNames = VehiclesListener.list().map { it.name }
        var msg = "Хочешь изменить отслеживание устройств?\n"
        msg += if (currentListenVehicleNames.isEmpty()) {
            "Сейчас я не отслеживаю ни одного устройства."
        } else {
            "Сейчас я отслеживаю устройства:\n ${currentListenVehicleNames.joinToString("\n")}"
        }

        msg
    },
    replyMarkup = {
        inlineKeyboard {
            subscribeSettingsMenu()
        }
    }
)

object RemoveVehicleEdit : EditMessage(
    message = {
        val currentListenVehicleNames = VehiclesListener.list().map { it.name }
        var msg = "Хочешь изменить отслеживание устройств?\n"
        msg += if (currentListenVehicleNames.isEmpty()) {
            "Сейчас я не отслеживаю ни одного устройства."
        } else {
            "Сейчас я отслеживаю устройства:\n ${currentListenVehicleNames.joinToString("\n")}"
        }

        msg
    },
    replyMarkup = {
        inlineKeyboard {
            subscribeSettingsMenu()
        }
    }
)