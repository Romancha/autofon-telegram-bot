package org.romancha.autofon.commons

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContextAndTwoTypesReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTwoTypesReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.CustomBehaviourContextAndTypeReceiver
import dev.inmo.tgbotapi.extensions.behaviour_builder.filters.MessageFilterByChat
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.CommonMessageFilter
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.command
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommandWithArgs
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.SimpleFilter
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.ByChatMessageMarkerFactory
import dev.inmo.tgbotapi.extensions.behaviour_builder.utils.marker_factories.MarkerFactory
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.TextContent
import dev.inmo.tgbotapi.types.message.content.TextMessage
import dev.inmo.tgbotapi.types.queries.callback.MessageDataCallbackQuery
import dev.inmo.tgbotapi.types.update.abstracts.Update
import kotlinx.coroutines.Job
import org.romancha.autofon.BotProps

val filterByChatId = CommonMessageFilter<TextContent> { it.chat.id.chatId == BotProps.chaId }
val filterByChatIdMsg = BehaviourContextAndTwoTypesReceiver<Boolean, Message, Update> { message, _ ->
    BotProps.chaId == message.chat.id.chatId
}
val filterByChaIdCallback = SimpleFilter<MessageDataCallbackQuery> { it.message.chat.id.chatId == BotProps.chaId }

suspend fun <BC : BehaviourContext> BC.onSecureCommand(
    command: String,
    requireOnlyCommandInMessage: Boolean = true,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, TextMessage, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in TextMessage, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTypeReceiver<BC, Unit, TextMessage>
): Job = command(
    command.toRegex(),
    requireOnlyCommandInMessage,
    filterByChatId,
    subcontextUpdatesFilter,
    markerFactory,
    scenarioReceiver
)

suspend fun <BC : BehaviourContext> BC.onSecureCommandWithArgs(
    command: String,
    subcontextUpdatesFilter: CustomBehaviourContextAndTwoTypesReceiver<BC, Boolean, TextMessage, Update> = MessageFilterByChat,
    markerFactory: MarkerFactory<in TextMessage, Any> = ByChatMessageMarkerFactory,
    scenarioReceiver: CustomBehaviourContextAndTwoTypesReceiver<BC, Unit, TextMessage, Array<String>>
): Job = onCommandWithArgs(command.toRegex(), filterByChatId, subcontextUpdatesFilter, markerFactory, scenarioReceiver)