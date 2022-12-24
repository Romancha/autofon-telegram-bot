package org.romancha.autofon.api.rest

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import mu.KotlinLogging
import org.romancha.autofon.BotProps
import org.romancha.autofon.format

object AutofonController {

    private val log = KotlinLogging.logger {}

    private val client = HttpClient(OkHttp) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
        install(ContentNegotiation) {
            json(format)
        }
    }

    suspend fun lastStates(): String {
        val states = executePostRequest("/jsonapi/laststates/")

        log.debug { "States: $states" }

        return states
    }

    suspend fun objects(): String {
        val objects = executePostRequest("/jsonapi/objects/")

        log.debug { "Objects: $objects" }

        return objects
    }

    private suspend fun executePostRequest(url: String): String {
        return try {
            client.request {
                method = HttpMethod.Post
                url {
                    protocol = URLProtocol.HTTP
                    host = BotProps.autofonHost
                    port = BotProps.autofonPort
                    path(url)
                    parameters.append("key", BotProps.key)
                    parameters.append("pwd", BotProps.pwd)
                }
            }.body()
        } catch (e: Exception) {
            log.error(e) { "Error while execute post request $url" }
            ""
        }
    }

}