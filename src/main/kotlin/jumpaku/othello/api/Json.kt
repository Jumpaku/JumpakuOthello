package jumpaku.othello.api

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import jumpaku.othello.game.*



fun updateData(json: JsonElement): Result<UpdateData> = result {
    UpdateData(
        json["gameId"].string,
        when (val n = json["move"].int) {
            -1 -> Move.Pass
            else -> Move.Place(Pos(n/8, n%8))
        }
    )
}

fun selectorInput(json: JsonElement): Result<SelectorInput> = result {
    SelectorInput(Disc.valueOf(json["selectPlayer"].string), json["board"].let {
        Board(
            it["darkDiscs"].array.fold(0uL) { d, n -> d or 1uL shl n.int },
            it["lightDiscs"].array.fold(0uL) { d, n -> d or 1uL shl n.int }
        )
    })
}

fun Pair<String, Game>.toJson(): JsonElement = this.let { (gameId, gameState) ->
    jsonObject("gameId" to gameId, "gameState" to gameState.toJson())
}

fun Move.toJson(): JsonElement = jsonObject("move" to when(this) {
    is Move.Pass -> -1
    is Move.Place -> pos.row*8 + pos.col
})

fun Game.toJson(): JsonElement = jsonObject(
     "board" to jsonObject(
        "darkDiscs" to jsonArray((0..63).filter { (1uL shl it) and phase.board.darkBits != 0uL }),
        "lightDiscs" to jsonArray((0..63).filter { (1uL shl it) and phase.board.lightBits != 0uL })
    ),
    "history" to jsonArray(history.mapNotNull {
        when (it) {
            is Move.Pass -> -1
            is Move.Place -> it.pos.row*8 + it.pos.col
        }
    })
) + when (phase) {
    is Phase.Completed -> mapOf(
        "state" to "Completed",
        "darkCount" to phase.darkCount,
        "lightCount" to phase.darkCount
    )
    is Phase.InProgress -> mapOf(
        "state" to "InProgress",
        "selectPlayer" to phase.player.name,
        "availableMoves" to jsonArray(phase.availableMoves.mapNotNull {
            when (it) {
                is Move.Pass -> -1
                is Move.Place -> it.pos.row * 8 + it.pos.col
            }
        })
    )
}
