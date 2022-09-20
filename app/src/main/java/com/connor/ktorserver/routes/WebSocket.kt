package com.connor.ktorserver.routes

import android.util.Log
import com.connor.ktorserver.App
import com.connor.ktorserver.utils.Connection
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.util.*
import kotlin.collections.LinkedHashSet

fun Route.webSocket() {
    route("/chat") {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket {
            Log.d("webSocket", "Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "webSocket: ${e.localizedMessage}")
            } finally {
                Log.i("INFO", "Removing $thisConnection!")
            }
        }
        webSocket("/send") {
            val file = File("${App.context.filesDir}/wsFile")
            kotlin.runCatching {
                for (frame in incoming) {
                    frame as? Frame.Binary ?: continue
                    val received = frame.data.inputStream()
                    received.source().buffer().use {
                        it.readAll(file.sink())
                    }
                }
            }
        }
    }
}