package com.connor.ktorserver.routes

import android.util.Log
import com.connor.ktorserver.App
import com.connor.ktorserver.utils.Connection
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.receiveAsFlow
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.util.*

fun Route.webSocket() {
    route("/chat") {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket {
            Log.d("webSocket", "Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("  There are ${connections.count()} users here.")
//                incoming.receiveAsFlow().filterIsInstance<Frame.Text>().collect {
//                    val textWithUsername = "[${thisConnection.name}]: ${it.readText()}"
//                    connections.forEach {
//                        it.session.send(textWithUsername)
//                    }
//                }
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
//                incoming.receiveAsFlow().filterIsInstance<Frame.Binary>().collect {
//                    val rec = it.data.inputStream()
//                    file.outputStream().use {
//                        rec.copyTo(it)
//                    }
//                }
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