package jumpaku.othello.api

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import jumpaku.commons.control.Failure
import jumpaku.commons.control.Success
import jumpaku.commons.control.result
import jumpaku.commons.json.parseJson
import jumpaku.othello.game.Board
import jumpaku.othello.game.Disc
import jumpaku.othello.game.Phase
import jumpaku.othello.selectors.AiSelector
import kotlin.random.Random


fun Application.modules() = routing {
    get("/") { call.respondText(ContentType.Text.Plain, HttpStatusCode.OK) { "Jumpaku Othello\n" } }
    route("/v1/") {
        get("/selectors/ai/move/") { call.getMoveByAiSelector() }
        post("/games/") {
            when (call.request.queryParameters["action"]) {
                "make" -> call.makeNewGame()
                "get" -> call.getGameState()
                "move" -> call.selectMove()
                else -> call.respondBadRequestQueryParameter("action")
            }
        }
    }
}

private suspend fun ApplicationCall.getMoveByAiSelector() {
    val player = when (request.queryParameters["player"]) {
        "dark" -> Disc.Dark
        "light" -> Disc.Light
        else -> return respondBadRequestQueryParameter("player")
    }
    val board = request.queryParameters["board"]?.run {
        Board(
            foldIndexed(0uL) { n, b, c -> b or if (c == 'd') 1uL shl n else 0uL },
            foldIndexed(0uL) { n, b, c -> b or if (c == 'l') 1uL shl n else 0uL }
        )
    } ?: return respondBadRequestQueryParameter("board")
    when (val move = result {
        AiSelector(Random.nextInt()).select(Phase.of(board, player))
    }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) {
            move.value.toJson().toString()
        }
        is Failure -> respondBadRequest(move.error.message ?: "")
    }
}

private suspend fun ApplicationCall.makeNewGame() {
    respondText(ContentType.Application.Json, HttpStatusCode.OK) {
        GameDatabase.make().let { gameId -> "{ \"gameId\" : \"$gameId\" }" }
    }
}

private suspend fun ApplicationCall.getGameState() {
    when (
        val game = receiveText().parseJson()
            .tryMap { it["gameId"].string }
            .tryFlatMap { gameId -> GameDatabase[gameId] }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) {
            game.value.toJson().toString()
        }
        is Failure -> respondNotFound(game.error.message ?: "")
    }
}

private suspend fun ApplicationCall.selectMove() {
    when (
        val result = receiveText().parseJson()
            .tryFlatMap { json -> updateData(json) }
            .tryFlatMap { (gameId, move) -> GameDatabase.update(gameId, move) }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) {
            result.value.toJson().toString()
        }
        is Failure -> respondNotFound(result.error.message ?: "")
    }
}

private suspend fun ApplicationCall.respondBadRequest(message: String) {
    respondText(ContentType.Application.Json, HttpStatusCode.BadRequest) {
        "{ \"message\": \"$message\" }"
    }
}

private suspend fun ApplicationCall.respondBadRequestQueryParameter(name: String) {
    respondBadRequest("invalid query parameter $name")
}

private suspend fun ApplicationCall.respondNotFound(message: String) {
    respondText(ContentType.Application.Json, HttpStatusCode.NotFound) {
        "{ \"message\": \"$message\" }"
    }
}