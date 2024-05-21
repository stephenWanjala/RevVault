package com.github.stephenwanjala.plugins

import com.github.stephenwanjala.mutex
import com.github.stephenwanjala.rateLimiters
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import kotlinx.coroutines.sync.withLock
import java.time.Instant

data class RateLimit(val timestamp: Instant, var count: Int)

fun Application.rateLimitingModule() {
    intercept(ApplicationCallPipeline.Plugins) {
        val ip = call.request.origin.remoteHost
        val rateLimit = rateLimiters[ip]
        val currentTime = Instant.now()

        if (rateLimit != null) {
            mutex.withLock {
                if (currentTime.isBefore(rateLimit.timestamp.plusSeconds(60))) {
                    if (rateLimit.count >= 5) {
                        call.respondText("Too Many Requests", status = HttpStatusCode.TooManyRequests)
                        finish()
                    } else {
                        rateLimit.count++
                    }
                } else {
                    rateLimiters[ip] = RateLimit(currentTime, 1)
                }
            }
        } else {
            rateLimiters[ip] = RateLimit(currentTime, 1)
        }
    }
}