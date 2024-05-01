package com.github.stephenwanjala.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*


fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ContentTransformationException>{ call,cause ->
            call.respond(HttpStatusCode.BadRequest, cause.localizedMessage)
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page Not Found", status = status)
        }
        
    }
}