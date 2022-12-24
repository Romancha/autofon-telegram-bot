package org.romancha.autofon.commons

import dev.inmo.tgbotapi.extensions.utils.types.buttons.InlineKeyboardBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.ReplyKeyboardBuilder
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.simpleButton
import dev.inmo.tgbotapi.utils.row

fun ReplyKeyboardBuilder.mainMenu() {
    row {
        simpleButton(Actions.CHARTS.title)
    }
    row {
        simpleButton(Actions.STATES_SUMMARY.title)
    }
    row {
        simpleButton(Actions.SUBSCRIBE_SETTINGS.title)
    }
}

fun InlineKeyboardBuilder.subscribeSettingsMenu() {
    row {
        dataButton(Actions.SUBSCRIBE_ADD.title, Actions.SUBSCRIBE_ADD.name)
        dataButton(Actions.SUBSCRIBE_REMOVE.title, Actions.SUBSCRIBE_REMOVE.name)
    }
}