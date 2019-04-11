package jumpaku.othello.api

import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        install(CallLogging)
        modules()
    }
    server.start(wait = true)
}
