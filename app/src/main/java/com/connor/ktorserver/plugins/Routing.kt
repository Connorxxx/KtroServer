package com.connor.ktorserver.plugins

import com.connor.ktorserver.routes.*
import com.connor.ktorserver.utils.SaveLogs
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.slf4j.event.Level
import java.io.File
import java.time.Duration

fun Application.configureRouting(file: File) {
    install(Compression)
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(CallLogging) {
        level = Level.INFO
        val log = StringBuilder()
        log.appendLine("<--------Start call response-------->")
        format { call ->
            call.request.apply {
                with(origin) {
                    log.appendLine("-->> [${method.value}] ${scheme}://${localHost}:${localPort}${uri}")
                }
                log.appendLine("-- Headers --")
                headers.forEach { key, value ->
                    log.appendLine("$key : ${value.joinToString(",")}")
                }
                log.appendLine("-- End Headers --")
                if (!call.parameters.isEmpty()) {
                    log.appendLine("-- Parameters --")
                    call.parameters.forEach { key, value ->
                        log.appendLine("$key : ${value.joinToString(",")}")
                    }
                    log.appendLine("-- End Parameters --")
                }
            }
            call.response.apply {
                log.appendLine("Status: ${status()}")
                with(call.request.origin) {
                    log.appendLine("<<-- [${call.response.status()?.value}] ${scheme}://${serverHost}:${serverPort}${uri}")
                }
                log.appendLine("-- Headers --")
                headers.allValues().forEach { key, value ->
                    log.appendLine("$key : ${value.joinToString(",")}")
                }
                log.appendLine("-- End Headers --")

            }
            SaveLogs.saveLogs(log.toString())
            log.setLength(0)
            log.toString()
        }
    }
    install(PartialContent)
    install(AutoHeadResponse)
    routing {
        customerRouting()
        listOrdersRoute()
        uploadFile()
        downloadFile()
        webSocket()
        get("/") {
            call.respond("Congratulation... you access ktor server")
        }
        get("/file") {
            call.respondFile(file)
        }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}