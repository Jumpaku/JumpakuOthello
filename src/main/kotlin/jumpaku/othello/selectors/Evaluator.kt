package jumpaku.othello.selectors

import jumpaku.commons.control.None
import jumpaku.commons.control.Some
import jumpaku.commons.control.orDefault
import jumpaku.othello.game.*
import java.lang.Math.pow
import kotlin.math.exp
import kotlin.math.expm1
import kotlin.math.max

class Evaluator {

    fun evaluate(game: Game, selectPlayer: Disc): Double {
        val state = game.state
        if (state is Game.State.Completed) return when {
            state.result is Game.Result.WinLose && state.result.winner.first == selectPlayer -> Double.MAX_VALUE
            state.result is Game.Result.WinLose && state.result.loser.first == selectPlayer -> Double.MIN_VALUE
            else -> 0.0
        }
        return evaluateFor(game, selectPlayer) - evaluateFor(game, selectPlayer.reverse())
    }

    companion object {

        val availableValues: Array<IntArray> = arrayOf(
            intArrayOf(100, -25, 6, 2, 2, 6, -25, 100),
            intArrayOf(-25, -40, 3, 4, 4, 3, -40, -25),
            intArrayOf(6, 3, 7, 6, 6, 7, 3, 6),
            intArrayOf(2, 4, 6, 8, 8, 6, 4, 2),
            intArrayOf(2, 4, 6, 8, 8, 6, 4, 2),
            intArrayOf(6, 3, 7, 6, 6, 7, 3, 6),
            intArrayOf(-25, -40, 3, 4, 4, 3, -40, -25),
            intArrayOf(100, -25, 6, 2, 2, 6, -25, 100)
        )

        val placedValues: Array<IntArray> = arrayOf(
            intArrayOf(100, -50, -2, -5, -5, -2, -50, 100),
            intArrayOf(-50, -70, -4, -4, -4, -4, -70, -50),
            intArrayOf(-2, -4, -1, -2, -2, -1, -4, -2),
            intArrayOf(-5, -4, -2, -1, -1, -2, -4, -5),
            intArrayOf(-5, -4, -2, -1, -1, -2, -4, -5),
            intArrayOf(-2, -4, -1, -2, -2, -1, -4, -2),
            intArrayOf(-50, -70, -4, -4, -4, -4, -70, -50),
            intArrayOf(100, -50, -2, -5, -5, -2, -50, 100)
        )
    }

    fun evaluateAvailable(game: Game, player: Disc): Double {
        val b = game.board
        val pA = b.availablePositions(player)
        val oA = b.availablePositions(player.reverse())
        val oFixed = b.findFixed(player.reverse())
        return b.availablePositions(player).map { pos ->
            val v0 = availableValues[pos.row][pos.col]
            val v = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> b[Pos(0, 0)].map { if (it == player) 30 else 1 }
                Pos(0, 6), Pos(1, 7) -> b[Pos(0, 7)].map { if (it == player) 30 else 1 }
                Pos(6, 0), Pos(7, 1) -> b[Pos(7, 0)].map { if (it == player) 30 else 1 }
                Pos(6, 7), Pos(7, 6) -> b[Pos(7, 7)].map { if (it == player) 30 else 1 }
                // X
                Pos(1, 1) -> b[Pos(0, 0)].map { if (it == player) 3 else 1 }
                Pos(1, 6) -> b[Pos(0, 7)].map { if (it == player) 3 else 1 }
                Pos(6, 1) -> b[Pos(7, 0)].map { if (it == player) 3 else 1 }
                Pos(6, 6) -> b[Pos(7, 7)].map { if (it == player) 3 else 1 }
                // Corner
                Pos(0, 0) ->
                    Some(v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 0)) }.intersect(oFixed).size)
                Pos(0, 7) ->
                    Some(v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 7)) }.intersect(oFixed).size)
                Pos(7, 0) ->
                    Some(v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 0)) }.intersect(oFixed).size)
                Pos(7, 7) ->
                    Some(v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 7)) }.intersect(oFixed).size)
                else -> None
            }.orDefault(v0)
            val c0 = if (pos in (pA - oA)) 1.2 else 1.0
            val c1 = if (v < 0) 0.5 else 1.0
            v * c0 * c1
        }.sum()
    }

    fun evaluatePlaced(game: Game, player: Disc): Double {
        val b = game.board
        val fixed = b.findFixed(player)
        val a0 = b.filterValues { it.map { it == player }.orDefault(false) }.keys.map { pos ->
            val v0 = placedValues[pos.row][pos.col]
            val v1 = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> b[Pos(0, 0)].map { if (it == player) 50 else -40 }
                Pos(0, 6), Pos(1, 7) -> b[Pos(0, 7)].map { if (it == player) 50 else -40 }
                Pos(6, 0), Pos(7, 1) -> b[Pos(7, 0)].map { if (it == player) 50 else -40 }
                Pos(6, 7), Pos(7, 6) -> b[Pos(7, 7)].map { if (it == player) 50 else -40 }
                // X
                Pos(1, 1) -> b[Pos(0, 0)].map { if (it == player) 20 else -20 }
                Pos(1, 6) -> b[Pos(0, 7)].map { if (it == player) 20 else -20 }
                Pos(6, 1) -> b[Pos(7, 0)].map { if (it == player) 20 else -20 }
                Pos(6, 6) -> b[Pos(7, 7)].map { if (it == player) 20 else -20 }
                //Corner
                Pos(0, 0) ->
                    Some(v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size)
                Pos(0, 7) ->
                    Some(v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size)
                Pos(7, 0) ->
                    Some(v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size)
                Pos(7, 7) ->
                    Some(v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size)
                else -> None
            }.orDefault(v0)
            if (pos in fixed) max(v1, 1) else v1
        }.sum()
        val a1 = fixed.run { val x = size/175.0; 100*175*(x + exp(-x) - 1) }
        return a1 + a0
    }

    private fun evaluateFor(game: Game, player: Disc): Double {
        return evaluatePlaced(game, player) + evaluateAvailable(game, player)
    }
}
