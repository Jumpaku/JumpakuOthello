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
        when (val s = json["move"].string) {
            "Pass" -> Move.Pass
            is String -> Move.Place(Pos(s[0] - '0', s[1] - '0'))
            else -> error("")
        }
    )
}

fun Move.toJson(): JsonElement = jsonObject("move" to when(this) {
    is Move.Pass -> "Pass"
    is Move.Place -> "${pos.row}${pos.col}"
})

fun Game.toJson(): JsonElement = jsonObject(
     "board" to jsonObject(
        "darkDiscs" to jsonArray((0..63).filter { (1uL shl it) and phase.board.darkBits != 0uL }),
        "lightDiscs" to jsonArray((0..63).filter { (1uL shl (64 - it)) and phase.board.lightBits != 0uL })
    ),
    "history" to jsonArray(history.mapNotNull {
        when (it) {
            is Move.Pass -> null
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
                is Move.Pass -> null
                is Move.Place -> it.pos.row * 8 + it.pos.col
            }
        })
    )
}
