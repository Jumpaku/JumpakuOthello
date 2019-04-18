package jumpaku.othello.api

import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import jumpaku.othello.game.Board
import jumpaku.othello.game.Disc
import jumpaku.othello.game.Move
import jumpaku.othello.game.Phase
import jumpaku.othello.selectors.AiSelector
import kotlin.random.Random

data class SelectorInput(val selectPlayer: Disc, val board: Board)

fun selectMoveByAi(input: SelectorInput): Result<Move> = result {
    AiSelector(Random.nextInt()).select(Phase.of(input.board, input.selectPlayer))
}