package org.romancha.autofon

import java.time.ZoneId
import java.util.*

object BotProps {

    val token: String
    val chaId: Long
    val key: String
    val pwd: String
    val dbFile: String
    val timeZone: ZoneId
    val autofonHost: String
    val autofonPort: Int
    val heartbeatEnabled: Boolean
    val heartbeatIntervalSeconds: Long
    val checkLastUpdateIntervalSeconds: Long

    init {
        val properties = Properties()

        val configStream = BotProps::class.java.classLoader.getResourceAsStream("config.yml")
        properties.load(configStream)

        BotProps::class.java.classLoader.getResourceAsStream("config_local.yml")?.let {
            properties.load(it)
        }

        token = System.getenv("AUTOFON_TELEGRAM_TOKEN") ?: properties.getProperty("token")
        chaId = System.getenv("AUTOFON_TELEGRAM_CHAT_ID")?.toLong()
            ?: properties.getProperty("chatId").toLong()

        key = System.getenv("AUTOFON_API_KEY") ?: properties.getProperty("apiKey")
        pwd = System.getenv("AUTOFON_API_PASSWORD") ?: properties.getProperty("apiPassword")

        dbFile = System.getenv("AUTOFON_DB_FILE") ?: properties.getProperty("dbFile")

        timeZone = ZoneId.of(System.getenv("AUTOFON_TIME_ZONE") ?: properties.getProperty("timeZone"))

        autofonHost = System.getenv("AUTOFON_HOST") ?: properties.getProperty("autofonHost")
        autofonPort = System.getenv("AUTOFON_PORT")?.toInt() ?: properties.getProperty("autofonPort").toInt()

        heartbeatEnabled =
            System.getenv("AUTOFON_HEARTBEAT_ENABLED")?.toBoolean() ?: properties.getProperty("heartbeatEnabled")
                .toBoolean()
        heartbeatIntervalSeconds = System.getenv("AUTOFON_HEARTBEAT_INTERVAL_SECONDS")?.toLong()
            ?: properties.getProperty("heartbeatIntervalSeconds").toLong()

        checkLastUpdateIntervalSeconds = System.getenv("AUTOFON_CHECK_LAST_UPDATE_INTERVAL_SECONDS")?.toLong()
            ?: properties.getProperty("checkLastUpdateIntervalSeconds").toLong()
    }
}