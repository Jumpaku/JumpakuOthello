package jumpaku.othello.api

import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.http.HttpMethod
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import jumpaku.othello.game.Game


fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        install(CallLogging)
        install(CORS) { anyHost() }
        modules()
    }
    server.start(wait = true)
}
