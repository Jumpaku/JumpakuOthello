package jumpaku.othello.selectors

import jumpaku.othello.game.Disc
import jumpaku.othello.game.Phase
import jumpaku.othello.game.Move
import jumpaku.othello.game.Pos
import jumpaku.othello.selectors.Evaluator.Companion.evaluateCompleted
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class AiSelector(seed: Int) : Selector {

    private val random = Random(seed)

    fun computeDepth(pos: Pos): Int = when(pos) {
        Pos(0, 1), Pos(1, 0), Pos(0, 6), Pos(1, 7),
        Pos(6, 0), Pos(7, 1), Pos(6, 7), Pos(7, 6),
        Pos(1, 1), Pos(1, 6), Pos(6, 1), Pos(6, 6) -> 4
        else -> 3
    }

    override fun select(phase: Phase): Move {
        require(phase is Phase.InProgress) { "phase must be InProgress" }
        val ms = phase.availableMoves
        val player = phase.player
        if (ms.size == 1) return ms.first()
        if (phase.progress < 2) return ms.shuffled(random).first()
        if (phase.progress >= 45) ms.find { isWinningGame(100, phase.move(it), player) }
        return ms.mapNotNull { it as? Move.Place }.maxBy { minmax(computeDepth(it.pos), phase.move(it), player) }!!
    }

    private fun isWinningGame(depth: Int, phase: Phase, selectPlayer: Disc): Boolean = depth >= 0 && when (phase) {
        is Phase.Completed -> phase.winner == selectPlayer
        is Phase.InProgress -> when {
            phase.board.fixedDiscs(selectPlayer).size > 32 -> true
            phase.player == selectPlayer -> phase.availableMoves.any { isWinningGame(depth - 1, phase.move(it), selectPlayer) }
            else -> phase.availableMoves.all { isWinningGame(depth - 1, phase.move(it), selectPlayer) }
        }
    }

    fun minmax(depth: Int, phase: Phase, selectPlayer: Disc): Double = when (phase) {
        is Phase.Completed -> evaluateCompleted(phase, selectPlayer)
        is Phase.InProgress ->
            if (depth == 0) Evaluator().evaluate(phase, selectPlayer)
            else phase.availableMoves.shuffled(random)
                .map { minmax(depth - 1, phase.move(it), selectPlayer) }
                .run { if (phase.player == selectPlayer) max() else min() }!!
    }

    fun alphabeta(
        depth: Int,
        phase: Phase,
        selectPlayer: Disc,
        alpha: Double = Double.MIN_VALUE,
        beta: Double = Double.MAX_VALUE
    ): Double = when (phase) {
        is Phase.Completed -> when (selectPlayer) {
            phase?.winner -> Double.MAX_VALUE
            phase?.loser -> Double.MIN_VALUE
            else -> 0.0
        }
        is Phase.InProgress -> when {
            depth == 0 -> Evaluator().evaluate(phase, selectPlayer)
            phase.player == selectPlayer -> phase.availableMoves
                .sortedByDescending { m -> alphabeta(0, phase.move(m), selectPlayer) }
                .fold(alpha) { a, m ->
                    max(a, alphabeta(depth - 1, phase.move(m), selectPlayer, a, beta))
                        .also { if (it >= beta) return@fold it }
                }
            else -> phase.availableMoves
                .sortedBy { m -> alphabeta(0, phase.move(m), selectPlayer) }
                .fold(beta) { b, m ->
                    min(b, alphabeta(depth - 1, phase.move(m), selectPlayer, alpha, b))
                        .also { if (it <= alpha) return@fold it }
                }
        }
    }
}

