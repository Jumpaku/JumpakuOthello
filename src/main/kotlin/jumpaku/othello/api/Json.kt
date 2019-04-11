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
    "board" to phase.board.run {
        (0..63).joinToString("") {
            when (val p = 1uL shl it) {
                darkBits and p -> "d"
                lightBits and p -> "l"
                else -> "_"
            }
        }
    },
    "availableMoves" to phase.availableMoves.joinToString("") {
        when (it) {
            is Move.Pass -> ""
            is Move.Place -> "${it.pos.row}${it.pos.col}"
        }
    },
    "history" to history.joinToString("") {
        when (it) {
            is Move.Pass -> ""
            is Move.Place -> "${it.pos.row}${it.pos.col}"
        }
    }
).apply {
    this += when (phase) {
        is Phase.Completed -> mapOf(
            "state" to "Completed",
            "darkCount" to phase.darkCount,
            "lightCount" to phase.darkCount
        )
        is Phase.InProgress -> mapOf(
            "state" to "InProgress",
            "selectPlayer" to phase.player.name
        )
    }
}