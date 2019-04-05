package jumpaku.othello.selectors

import jumpaku.othello.game.Disc
import jumpaku.othello.game.Game


class Evaluator {

    val availableValues: Array<IntArray> = arrayOf(
        intArrayOf(100,-10, 5, 1, 1, 5,-10,100),
        intArrayOf(-10,-40, 1, 3, 3, 1,-40,-10),
        intArrayOf(  5,  1, 5, 3, 3, 4,  1,  5),
        intArrayOf(  1,  2, 3, 4, 4, 3,  2,  1),
        intArrayOf(  1,  2, 3, 4, 4, 3,  2,  1),
        intArrayOf(  5,  1, 5, 3, 3, 5,  1,  5),
        intArrayOf(-10,-40, 1, 2, 2, 1,-40,-10),
        intArrayOf(100,-10, 5, 1, 1, 5,-10,100))

    val placedValues: Array<IntArray> = arrayOf(
        intArrayOf(100,-50, 0,-4,-4, 0,-50,100),
        intArrayOf(-50,-90,-4,-3,-3,-4,-90,-50),
        intArrayOf(  0, -4, 0,-2,-2, 0, -4,  0),
        intArrayOf( -4, -3,-2,-1,-1,-2, -3, -4),
        intArrayOf( -4, -3,-2,-1,-1,-2, -3, -4),
        intArrayOf(  0, -4, 0,-2,-2, 0, -4,  0),
        intArrayOf(-50,-90,-4,-3,-3,-4,-90,-50),
        intArrayOf(100,-50, 0,-4,-4, 0,-50,100))


    fun evaluate(game: Game, selectPlayer: Disc): Int {
        val state = game.state
        if (state is Game.State.Completed) return when {
            state.result is Game.Result.WinLose && state.result.winner == selectPlayer -> Int.MAX_VALUE
            state.result is Game.Result.WinLose && state.result.loser == selectPlayer -> Int.MIN_VALUE
            else -> 0
        }
        return evaluateFor(game, selectPlayer) - evaluateFor(game, selectPlayer.reverse())
    }

    private fun evaluateFor(game: Game, player: Disc): Int {
        val placed = game.board.flatMap { (pos, v) -> v.filter { it == player }.map { placedValues[pos.row][pos.col] } }.sum()
        val available = game.board.availablePositions(player).map { (i, j) -> availableValues[i][j] }.sum()
        ++c
        placedValueSum += placed
        availableValueSum += available
        return 1 * available// + 1 * placed// +
    }
}

var c = 0L
var placedValueSum = 0L
var availableValueSum = 0L