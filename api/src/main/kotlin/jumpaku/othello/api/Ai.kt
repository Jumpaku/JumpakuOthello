package jumpaku.othello.api

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.toJsonArray
import com.google.gson.JsonElement
import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import jumpaku.othello.game.Board
import jumpaku.othello.game.Disc
import jumpaku.othello.game.Move
import jumpaku.othello.game.Phase
import jumpaku.othello.selectors.AiSelector
import kotlin.random.Random

data class SelectorInput(val selectPlayer: Disc, val board: Board)

data class EvaluatedMoves(val moves: Map<Move, Double>) {
    fun toJson(): JsonElement = jsonObject(
        "moves" to moves.map { (move, value) ->
            jsonObject(
                "move" to when (move) {
                    is Move.Pass -> -1
                    is Move.Place -> move.pos.row * 8 + move.pos.col
                },
                "evaluation" to "%.3f".format(value)
            )
        }.toJsonArray()
    )
}

fun selectMoveByAi(input: SelectorInput): Result<Move> = result {
    AiSelector(Random.nextInt()).select(Phase.of(input.board, input.selectPlayer))
}

fun evaluateMovesByAi(input: SelectorInput): Result<EvaluatedMoves> = result {
    val moves = AiSelector(Random.nextInt()).evaluateMoves(Phase.of(input.board, input.selectPlayer))
    EvaluatedMoves(moves)
}