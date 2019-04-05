package jumpaku.othello.selectors

import jumpaku.othello.game.Disc
import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import kotlin.random.Random

class AiSelector : Selector {

    fun computeDepth(game: Game): Int {
        val a = game.board.availablePositions(Disc.Dark).size
        val b = game.board.availablePositions(Disc.Light).size
        fun estimateDepth(threshold: Int, atMost: Int): Int = sequence {
            var x = 1
            repeat(30) {
                x *= a
                yield(x)
                x *= b
                yield(x)
            }
        }.takeWhile { it < threshold }.count().coerceAtMost(atMost)
        return when(game.progress) {
            0 -> 1
            in 1..5 -> 3
            in 6..17 -> estimateDepth(300, 3)
            in 18..30 -> 2
            in 31..42 -> estimateDepth(500, 3)
            in 43..47 -> estimateDepth(1000, 3)
            in 48..52 -> estimateDepth(1000, 5)
            else -> 8
        }
    }

    override fun select(game: Game): Move {
        require(game.state is Game.State.WaitingMove)
        val ms = game.availableMoves
        if (ms.size == 1) return ms.first()
        val player = game.state.player
        val depth = computeDepth(game)
        if (depth == 8) return ms.find { isWinningGame(7, game.move(it), player) }
                ?: ms.maxBy { minmax(8, game.move(it), player) }!!
        return ms.maxBy { minmax(depth, game.move(it), player) }!!
    }
}


fun isWinningGame(depth: Int, game: Game, selectPlayer: Disc): Boolean = depth >= 0 && when (game.state) {
    is Game.State.Completed -> (game.state.result as? Game.Result.WinLose)?.winner?.first == selectPlayer
    is Game.State.WaitingMove -> when (selectPlayer) {
        game.state.player -> game.availableMoves.any { isWinningGame(depth - 1, game.move(it), selectPlayer) }
        else -> game.availableMoves.all { isWinningGame(depth - 1, game.move(it), selectPlayer) }
    }
}

private val random = Random(1089)

fun minmax(depth: Int, game: Game, selectPlayer: Disc): Int = when (game.state) {
    is Game.State.Completed -> when (game.state.result) {
        is Game.Result.Tie -> 0
        is Game.Result.WinLose -> when (selectPlayer) {
            game.state.result.winner.first -> Int.MAX_VALUE
            game.state.result.loser.first -> Int.MIN_VALUE
            else -> error("")
        }
    }
    is Game.State.WaitingMove -> {
        if (depth == 0) Evaluator().evaluate(game, selectPlayer)
        else game.availableMoves.shuffled(random)
            .map { minmax(depth - 1, game.move(it), selectPlayer) }
            .run { if (game.state.player == selectPlayer) max() else min() }!!
    }
}
