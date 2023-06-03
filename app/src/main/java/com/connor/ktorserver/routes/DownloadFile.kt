package com.connor.ktorserver.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.downloadFile() {
    route("/download") {
        get("{path...}") {
            val path = call.parameters.getAll("path")?.joinToString("/")
            path?.let {
                val file = File(path)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        path
                    ).toString()
                )
                call.respondFile(file)
            } ?: call.respondText("Missing path", status = HttpStatusCode.NotFound)
        }
    }
}