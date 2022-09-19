package com.connor.ktorserver.plugins

import com.connor.ktorserver.utils.SaveLogs
import io.ktor.server.application.*
import io.ktor.server.plugins.*

val RequestLoggingPlugin = createApplicationPlugin(name = "RequestLoggingPlugin") {
    onCall {  call ->
        call.request.apply {
            val log = StringBuilder()

            with(origin) {
                log.appendLine("-->> [${method.value}] ${scheme}://${host}:${port}${uri}")
            }

            if (!headers.isEmpty()) {
                log.appendLine("-- Headers --")
                headers.forEach { key, value ->
                    log.appendLine("$key : ${value.joinToString(",")}")
                }
                log.appendLine("-- End Headers --")
            }

            if (!call.parameters.isEmpty()) {
                log.appendLine("-- Parameters --")
                call.parameters.forEach { key, value ->
                    log.appendLine("$key : ${value.joinToString(",")}")
                }
                log.appendLine("-- End Parameters --")
            }

            SaveLogs.saveLogs(log.toString())
        }
    }

    onCallRespond { call ->
        val status = call.response.status()
        call.response.apply {
            val log = StringBuilder()

            with(call.request.origin) {
                log.appendLine("<<-- [${status}] ${scheme}://${host}:${port}${uri}")
            }

            if (!headers.allValues().isEmpty()) {
                log.appendLine("-- Headers --")
                headers.allValues().forEach { key, value ->
                    log.appendLine("$key : ${value.joinToString(",")}")
                }
                log.appendLine("-- End Headers --")
            }


            transformBody { data ->
                log.appendLine("-- Body --")
                log.appendLine(data)
                log.appendLine("-- End Body --")
                data
            }

            SaveLogs.saveLogs(log.toString())
        }
    }
}