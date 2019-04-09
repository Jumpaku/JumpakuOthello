package jumpaku.othello.selectors

import jumpaku.commons.control.None
import jumpaku.commons.control.Some
import jumpaku.commons.control.orDefault
import jumpaku.othello.game.*
import jumpaku.timeAvailable
import jumpaku.timeEvalPlaced
import jumpaku.timeEvaluate
import jumpaku.timePlace
import java.lang.Math.pow
import kotlin.math.exp
import kotlin.math.expm1
import kotlin.math.max

var timeAvailablePositions = 0.0
var timeFindFixed = 0.0
var timeSumAvailable = 0.0
var timeSumPlaced = 0.0
class Evaluator {

    fun evaluate(game: Game, selectPlayer: Disc): Double {
        val s = System.nanoTime()
        val state = game.state
        ++c
        if (state is Game.State.Completed) return when {
            state.result is Game.Result.WinLose && state.result.winner.first == selectPlayer -> Double.MAX_VALUE
            state.result is Game.Result.WinLose && state.result.loser.first == selectPlayer -> Double.MIN_VALUE
            else -> 0.0
        }
        val e = evaluateFor(game, selectPlayer) - evaluateFor(game, selectPlayer.reverse())
        timeEvaluate += (System.nanoTime() - s)*1e-9
        //println("\t$e")
        return e
    }

    companion object {

        val availableValues: Array<IntArray> = arrayOf(
            intArrayOf(100, -30, 6, 2, 2, 6, -30, 100),
            intArrayOf(-30, -50, 3, 4, 4, 3, -50, -30),
            intArrayOf(6, 3, 7, 6, 6, 7, 3, 6),
            intArrayOf(2, 4, 6, 8, 8, 6, 4, 2),
            intArrayOf(2, 4, 6, 8, 8, 6, 4, 2),
            intArrayOf(6, 3, 7, 6, 6, 7, 3, 6),
            intArrayOf(-30, -50, 3, 4, 4, 3, -50, -30),
            intArrayOf(100, -30, 6, 2, 2, 6, -30, 100)
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
        val s0 = System.nanoTime()
        val pA = b.availablePositions(player)
        val oA = b.availablePositions(player.reverse())
        val s1 = System.nanoTime()
        val oFixed = b.fixedDiscs(player.reverse())
        val s2 = System.nanoTime()
        val pNegative = setOf(
            Pos(0, 1), Pos(1, 0),
            Pos(0, 6), Pos(1, 7),
            Pos(6, 0), Pos(7, 1),
            Pos(6, 7), Pos(7, 6),
            Pos(1, 1), Pos(1, 6), Pos(6, 1), Pos(6, 6))
        val r = pA.map { pos ->
            val v0 = availableValues[pos.row][pos.col]
            val v = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> b[Pos(0, 0)]?.let { if (it == player) 30 else 1 }
                Pos(0, 6), Pos(1, 7) -> b[Pos(0, 7)]?.let { if (it == player) 30 else 1 }
                Pos(6, 0), Pos(7, 1) -> b[Pos(7, 0)]?.let { if (it == player) 30 else 1 }
                Pos(6, 7), Pos(7, 6) -> b[Pos(7, 7)]?.let { if (it == player) 30 else 1 }
                // X
                Pos(1, 1) -> b[Pos(0, 0)]?.let { if (it == player) 3 else 1 }
                Pos(1, 6) -> b[Pos(0, 7)]?.let { if (it == player) 3 else 1 }
                Pos(6, 1) -> b[Pos(7, 0)]?.let { if (it == player) 3 else 1 }
                Pos(6, 6) -> b[Pos(7, 7)]?.let { if (it == player) 3 else 1 }
                // Corner
                Pos(0, 0) -> v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 0)) }.intersect(oFixed).size
                Pos(0, 7) -> v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 7)) }.intersect(oFixed).size
                Pos(7, 0) -> v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 0)) }.intersect(oFixed).size
                Pos(7, 7) -> v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 7)) }.intersect(oFixed).size
                else -> null
            } ?: v0
            val c = if (pos in (pA - oA)) 1.0 else 1.0
            val b = if (pos in pNegative) (pA - pNegative).size*0 else 0
            v * c + b
        }.sum()
        val s3 = System.nanoTime()
        timeAvailablePositions += (s1 - s0)*1e-9
        timeFindFixed += (s2 - s1)*1e-9
        timeSumAvailable += (s3 - s2)*1e-9
        return r
    }

    fun evaluatePlaced(game: Game, player: Disc): Double {
        val b = game.board
        val s0 = System.nanoTime()
        val fixed = b.fixedDiscs(player)
        val s1 = System.nanoTime()
        val a0 = Pos.enumerate.filter { b[it] == player }.map { pos ->
            val v0 = placedValues[pos.row][pos.col]
            val v1 = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> b[Pos(0, 0)]?.let { if (it == player) 50 else -40 }
                Pos(0, 6), Pos(1, 7) -> b[Pos(0, 7)]?.let { if (it == player) 50 else -40 }
                Pos(6, 0), Pos(7, 1) -> b[Pos(7, 0)]?.let { if (it == player) 50 else -40 }
                Pos(6, 7), Pos(7, 6) -> b[Pos(7, 7)]?.let { if (it == player) 50 else -40 }
                // X
                Pos(1, 1) -> b[Pos(0, 0)]?.let { if (it == player) 20 else -20 }
                Pos(1, 6) -> b[Pos(0, 7)]?.let { if (it == player) 20 else -20 }
                Pos(6, 1) -> b[Pos(7, 0)]?.let { if (it == player) 20 else -20 }
                Pos(6, 6) -> b[Pos(7, 7)]?.let { if (it == player) 20 else -20 }
                //Corner
                Pos(0, 0) -> v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(0, 7) -> v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(7, 0) -> v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(7, 7) -> v0 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                else -> null
            } ?: v0
            if (pos in fixed) max(v1, 1) else v1
        }.sum()
        val s2 = System.nanoTime()
        val a1 = fixed.run { val x = size/175.0; 100*175*(x + exp(-x) - 1) }
        timeFindFixed += (s1 - s0)*1e-9
        timeSumPlaced += (s2 - s1)*1e-9
        return a1 + a0
    }

    private fun evaluateFor(game: Game, player: Disc): Double {
        val s0 = System.nanoTime()
        val ep = evaluatePlaced(game, player)
        val s1 = System.nanoTime()
        val ea = evaluateAvailable(game, player)
        val s2 = System.nanoTime()
        timeEvalPlaced += (s1-s0)*1e-9
        timeAvailable += (s2-s1)*1e-9
        return ep + ea
    }
}
