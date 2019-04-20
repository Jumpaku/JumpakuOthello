package jumpaku.othello.selectors

import jumpaku.othello.game.*
import kotlin.math.max

class Evaluator {

    fun evaluate(phase: Phase, selectPlayer: Disc): Double = when (phase) {
        is Phase.Completed -> evaluateCompleted(phase, selectPlayer)
        is Phase.InProgress-> evaluateFor(phase, selectPlayer) - evaluateFor(phase, selectPlayer.reverse())
    }

    companion object {

        fun evaluateCompleted(result: Phase.Completed, selectPlayer: Disc): Double = when (selectPlayer) {
            result.winner -> Double.MAX_VALUE
            result.loser -> Double.MIN_VALUE
            else -> 0.0
        }

        val availableValues: Array<IntArray> = arrayOf(
            intArrayOf(100, -5, 5, 3, 3, 5, -5, 100),
            intArrayOf(-5, -8, 4, 6, 6, 4, -8, -5),
            intArrayOf(5, 4, 7, 8, 8, 7, 4, 5),
            intArrayOf(3, 6, 8, 9, 9, 8, 6, 3),
            intArrayOf(3, 6, 8, 9, 9, 8, 6, 3),
            intArrayOf(5, 4, 7, 8, 8, 7, 4, 5),
            intArrayOf(-5, -8, 4, 6, 6, 4, -8, -5),
            intArrayOf(100, -5, 5, 3, 3, 5, -5, 100)
        )

        val placedValues: Array<IntArray> = arrayOf(
            intArrayOf(100, -40, -3, -5, -5, -3, -40, 100),
            intArrayOf(-40, -80, -5, -4, -4, -5, -80, -40),
            intArrayOf(-3, -5, -2, -3, -3, -2, -5, -3),
            intArrayOf(-5, -4, -3, -1, -1, -3, -4, -5),
            intArrayOf(-5, -4, -3, -1, -1, -3, -4, -5),
            intArrayOf(-3, -5, -2, -3, -3, -2, -5, -3),
            intArrayOf(-40, -80, -5, -4, -4, -5, -80, -40),
            intArrayOf(100, -40, -3, -5, -5, -3, -40, 100)
        )
    }

    fun evaluateAvailable(phase: Phase, player: Disc): Double {
        val board = phase.board
        val pA = board.availablePos(player)
        val oA = board.availablePos(player.reverse())
        val oFixed = board.fixedDiscs(player.reverse())
        return pA.map { pos ->
            val v0 = availableValues[pos.row][pos.col]
            val v = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> board[Pos(0, 0)]?.let { if (it == player) 15 else 2 } ?: v0
                Pos(0, 6), Pos(1, 7) -> board[Pos(0, 7)]?.let { if (it == player) 15 else 2 } ?: v0
                Pos(6, 0), Pos(7, 1) -> board[Pos(7, 0)]?.let { if (it == player) 15 else 2 } ?: v0
                Pos(6, 7), Pos(7, 6) -> board[Pos(7, 7)]?.let { if (it == player) 15 else 2 } ?: v0
                // X
                Pos(1, 1) -> board[Pos(0, 0)]?.let { if (it == player) 7 else 1 } ?: v0
                Pos(1, 6) -> board[Pos(0, 7)]?.let { if (it == player) 7 else 1 } ?: v0
                Pos(6, 1) -> board[Pos(7, 0)]?.let { if (it == player) 7 else 1 } ?: v0
                Pos(6, 6) -> board[Pos(7, 7)]?.let { if (it == player) 7 else 1 } ?: v0
                // Corner
                Pos(0, 0) -> v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 0)) }.intersect(oFixed).size
                Pos(0, 7) -> v0 - 4 * (0..7).flatMap { setOf(Pos(0, it), Pos(it, 7)) }.intersect(oFixed).size
                Pos(7, 0) -> v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 0)) }.intersect(oFixed).size
                Pos(7, 7) -> v0 - 4 * (0..7).flatMap { setOf(Pos(7, it), Pos(it, 7)) }.intersect(oFixed).size
                else -> v0
            }
            v * (if (pos in (pA - oA)) 1.2 else 1.0)
        }.sum()
    }

    fun evaluatePlaced(phase: Phase, player: Disc): Double {
        val b = phase.board
        val fixed = b.fixedDiscs(player)
        val v = Pos.enumerate.filter { b[it] == player }.map { pos ->
            val v0 = placedValues[pos.row][pos.col]
            val v1 = when (pos) {
                // C
                Pos(0, 1), Pos(1, 0) -> b[Pos(0, 0)]?.let { if (it == player) 50 else -20 } ?: v0
                Pos(0, 6), Pos(1, 7) -> b[Pos(0, 7)]?.let { if (it == player) 50 else -20 } ?: v0
                Pos(6, 0), Pos(7, 1) -> b[Pos(7, 0)]?.let { if (it == player) 50 else -20 } ?: v0
                Pos(6, 7), Pos(7, 6) -> b[Pos(7, 7)]?.let { if (it == player) 50 else -20 } ?: v0
                // X
                Pos(1, 1) -> b[Pos(0, 0)]?.let { if (it == player) 25 else -25 } ?: v0
                Pos(1, 6) -> b[Pos(0, 7)]?.let { if (it == player) 25 else -25 } ?: v0
                Pos(6, 1) -> b[Pos(7, 0)]?.let { if (it == player) 25 else -25 } ?: v0
                Pos(6, 6) -> b[Pos(7, 7)]?.let { if (it == player) 25 else -25 } ?: v0
                //Corner
                Pos(0, 0) -> 100 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(0, 7) -> 100 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(7, 0) -> 100 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                Pos(7, 7) -> 100 + 4 * (0..7).flatMap { setOf(Pos(pos.row, it), Pos(it, pos.col)) }.intersect(fixed).size
                else -> v0
            }
            if (pos in fixed) max(v1, 7) else v1
        }.sum()
        return v.toDouble()// + a1
    }

    fun evaluateExposed(phase: Phase, player: Disc): Double {
        val b = phase.board
        return Pos.enumerate.filter { b[it] == player }.map { pos ->
            val p = pos.bits
            val blanks = b.run { darkBits or lightBits }.inv()
            val mask = ((p shl 8) and 0xffffffffffffff00uL) or
                    ((p shl 7) and 0x7f7f7f7f7f7f7f00uL) or
                    ((p shr 1) and 0x7f7f7f7f7f7f7f7fuL) or
                    ((p shr 9) and 0x007f7f7f7f7f7f7fuL) or
                    ((p shr 8) and 0x00ffffffffffffffuL) or
                    ((p shr 7) and 0x00fefefefefefefeuL) or
                    ((p shl 1) and 0xfefefefefefefefeuL) or
                    ((p shl 9) and 0xfefefefefefefe00uL)
            -1.0 * countBits(blanks and mask)
        }.sum()
    }

    private fun evaluateFor(phase: Phase, player: Disc): Double {
        val ep = evaluatePlaced(phase, player)
        val ea = evaluateAvailable(phase, player)
        val ee = evaluateExposed(phase, player)
        return ep + ea
    }
}
