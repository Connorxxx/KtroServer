package com.connor.ktorserver.plugins

import com.connor.ktorserver.routes.customerRouting
import com.connor.ktorserver.routes.listOrdersRoute
import com.connor.ktorserver.utils.SaveLogs
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import java.io.File

fun Application.configureRouting(file: File) {
    install(Compression)
    install(CallLogging) {
        level = Level.INFO
        val log = StringBuilder()
        log.appendLine("<--------Start call response-------->")
        format { call ->
            call.request.apply {
                with(origin) {
                    log.appendLine("-->> [${method.value}] ${scheme}://${host}:${port}${uri}")
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
                    log.appendLine("<<-- [${call.response.status()?.value}] ${scheme}://${host}:${port}${uri}")
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
    routing {
        customerRouting()
        listOrdersRoute()
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