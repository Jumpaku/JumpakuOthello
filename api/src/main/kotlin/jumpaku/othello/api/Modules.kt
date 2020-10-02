package jumpaku.othello.api

import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.string
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.request.receiveText
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import jumpaku.commons.control.Failure
import jumpaku.commons.control.Success
import jumpaku.commons.json.parseJson


fun Application.apiModules() = routing {
    get("/") { call.respondText(ContentType.Text.Plain, HttpStatusCode.OK) { "Jumpaku Othello\n" } }
    route("/v1/") {
        get("/") { call.respondRedirect("/v1/api/") }
        get("/api") { call.respondText(ContentType.Text.Plain, HttpStatusCode.OK) { "Jumpaku Othello API v1\n" } }
        post("/ai/move/") { call.selectMoveByAi() }
        post("/ai/moves/") { call.evaluateMovesByAi() }
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

private suspend fun ApplicationCall.selectMoveByAi() {
    when(
        val result = receiveText().parseJson()
            .tryFlatMap { selectorInput(it) }
            .tryFlatMap { input -> selectMoveByAi(input) }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) { result.value.toJson().toString() }
        is Failure -> respondBadRequest(result.error.message ?: "")
    }
}

private suspend fun ApplicationCall.evaluateMovesByAi() {
    println("OK")
    when(
        val result = receiveText().parseJson()
            .tryFlatMap { selectorInput(it) }
            .tryFlatMap { input -> evaluateMovesByAi(input) }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) { result.value.toJson().toString() }
        is Failure -> respondBadRequest(result.error.message ?: "")
    }
}

private suspend fun ApplicationCall.makeNewGame() {
    respondText(ContentType.Application.Json, HttpStatusCode.OK) { GameDatabase.make().toJson().toString() }
}

private suspend fun ApplicationCall.getGameState() {
    when (
        val result = receiveText().parseJson()
            .tryMap { it["gameId"].string }
            .tryFlatMap { gameId -> GameDatabase[gameId] }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) { result.value.toJson().toString() }
        is Failure -> respondNotFound(result.error.message ?: "")
    }
}

private suspend fun ApplicationCall.selectMove() {
    when (
        val result = receiveText().parseJson()
            .tryFlatMap { json -> updateData(json) }
            .tryFlatMap { (gameId, move) -> GameDatabase.update(gameId, move) }) {
        is Success -> respondText(ContentType.Application.Json, HttpStatusCode.OK) { result.value.toJson().toString() }
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