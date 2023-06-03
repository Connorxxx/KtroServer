package com.connor.ktorserver.routes

import com.connor.ktorserver.App
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import java.io.File

fun Route.uploadFile() {
    route("/upload") {
        post("text") {
            val text = call.receiveText()
            call.respondText(text)
        }
        post("channel") {
            val readChannel = call.receiveChannel()
            val text = readChannel.readRemaining().readText()
            call.respondText(text)
        }
        post("file") {
            val file = File("${App.context.filesDir}/file")
            call.receiveChannel().copyAndClose(file.writeChannel())
            call.respondText("A file is uploaded")
        }
        post("/signup") {
            val formParameters = call.receiveParameters()
            val username = formParameters["username"].toString()
            call.respondText("The '$username' account is created")
        }
        var fileDescription = ""
        var fileName = ""
        post {
            val multipartData = call.receiveMultipart()
            val contentLength = call.request.header(HttpHeaders.ContentLength)

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        fileDescription = part.value
                    }
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("${App.context.filesDir}/$fileName").writeBytes(fileBytes)
                    }
                    else -> {}
                }
                part.dispose
            }
            call.respondText("$fileDescription is uploaded to '${App.context.filesDir}/$fileName' $contentLength")
        }
    }
}