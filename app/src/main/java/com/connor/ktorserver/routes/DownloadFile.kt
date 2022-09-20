package com.connor.ktorserver.routes

import com.connor.ktorserver.App
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.downloadFile() {
    route("/download") {
        get {
            val file = File("${App.context.filesDir}/upload_file.jpg")
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(
                    ContentDisposition.Parameters.FileName,
                    "upload_file.jpg"
                ).toString()
            )
            call.respondFile(file)
        }
    }
}