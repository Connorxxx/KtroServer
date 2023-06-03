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
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        path
                    ).toString()
                )
                call.respondFile(File(path))
            } ?: call.respondText("Missing path", status = HttpStatusCode.NotFound)
        }
    }
}