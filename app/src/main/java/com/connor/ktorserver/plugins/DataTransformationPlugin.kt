package com.connor.ktorserver.plugins

import android.util.Log
import com.connor.ktorserver.models.Customer
import com.connor.ktorserver.utils.SaveLogs
import io.ktor.server.application.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val DataTransformationPlugin = createApplicationPlugin(name = "DataTransformationPlugin") {

    onCallReceive { call ->
        val log = StringBuilder()
        log.appendLine("Status: ${call.response.status()}")
        transformBody { data ->
            log.appendLine("-- onCallReceive --")
            if (requestedType?.type == Customer::class) {
                data.toInputStream().bufferedReader().forEachLine {
                    log.appendLine(it)
                }
            } else {
                data
            }

        }
        SaveLogs.saveLogs(log.toString())
    }
    onCallRespond { _ ->
        val log = StringBuilder()
        transformBody { data ->
            log.appendLine("-- onCallRespond --")
            log.appendLine(data)
            log.appendLine("-- End onCallRespond --")
            data
        }
        SaveLogs.saveLogs(log.toString())
    }
}