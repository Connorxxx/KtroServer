package com.connor.ktorserver.plugins

import com.connor.ktorserver.routes.customerRouting
import com.connor.ktorserver.routes.listOrdersRoute
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting(file: File) {
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