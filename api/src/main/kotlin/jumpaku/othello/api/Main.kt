package jumpaku.othello.api

import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CORS
import io.ktor.features.CachingHeaders
import io.ktor.features.CallLogging
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.content.*
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        install(CallLogging)
        install(CORS) { anyHost() }
        install(CachingHeaders) {
            options { outgoingContent ->
                outgoingContent.contentType?.withoutParameters()?.also {
                    if (it.match(ContentType.Image.Any)) return@options CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                }
                null
            }
        }
        routing {
            static("/app") {
                resources("/dist")
                defaultResource("/dist/index.html")
            }
        }
        apiModules()
    }
    server.start(wait = true)
}
