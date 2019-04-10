package jumpaku.othello.selectors

import jumpaku.othello.game.Disc
import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import jumpaku.othello.game.Pos
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

var countIsWinningGame = 0
class AiSelector(seed: Int) : Selector {

    private val random = Random(seed)

    fun computeDepth(pos: Pos): Int = when(pos) {
        Pos(0, 1), Pos(1, 0), Pos(0, 6), Pos(1, 7),
        Pos(6, 0), Pos(7, 1), Pos(6, 7), Pos(7, 6),
        Pos(1, 1), Pos(1, 6), Pos(6, 1), Pos(6, 6) -> 4
        else -> 3
    }

    override fun select(game: Game): Move {
        require(game.state is Game.State.WaitingMove)
        val ms = game.availableMoves
        val player = game.state.player
        if (ms.size == 1) return ms.first()
        if (game.progress < 2) return ms.shuffled(random).first()
        if (game.progress >= 45) ms.find { isWinningGame(100, game.move(it), player) }?.let { println("WIN");return it }
        return ms.mapNotNull { it as? Move.Place }.maxBy { minmax(computeDepth(it.pos), game.move(it), player) }!!
    }

    fun isWinningGame(depth: Int, game: Game, selectPlayer: Disc): Boolean {
        countIsWinningGame++
        return depth >= 0 && when (val s = game.state) {
            is Game.State.Completed -> (s.result as? Game.Result.WinLose)?.winner?.first == selectPlayer
            is Game.State.WaitingMove -> when {
                game.board.fixedDiscs(selectPlayer).size > 32 -> true
                s.player == selectPlayer -> game.availableMoves.any { isWinningGame(depth - 1, game.move(it), selectPlayer) }
                else -> game.availableMoves.all { isWinningGame(depth - 1, game.move(it), selectPlayer) }
            }
        }
    }

    fun minmax(depth: Int, game: Game, selectPlayer: Disc): Double = when (val s = game.state) {
        is Game.State.Completed -> when {
            s.result is Game.Result.WinLose && selectPlayer == s.result.winner.first -> Double.MAX_VALUE
            s.result is Game.Result.WinLose && selectPlayer == s.result.loser.first -> Double.MIN_VALUE
            else -> 0.0
        }
        is Game.State.WaitingMove ->
            if (depth == 0) Evaluator().evaluate(game, selectPlayer)
            else game.availableMoves.shuffled(random)
                .map { minmax(depth - 1, game.move(it), selectPlayer) }
                .run { if (s.player == selectPlayer) max() else min() }!!
    }

    fun alphabeta(
        depth: Int,
        game: Game,
        selectPlayer: Disc,
        alpha: Double = Double.MIN_VALUE,
        beta: Double = Double.MAX_VALUE
    ): Double = when (game.state) {
        is Game.State.Completed -> when (selectPlayer) {
            (game.state.result as? Game.Result.WinLose)?.winner?.first -> Double.MAX_VALUE
            (game.state.result as? Game.Result.WinLose)?.loser?.first -> Double.MIN_VALUE
            else -> 0.0
        }
        is Game.State.WaitingMove -> when {
            depth == 0 -> Evaluator().evaluate(game, selectPlayer)
            game.state.player == selectPlayer -> game.availableMoves
                .sortedByDescending { m -> alphabeta(0, game.move(m), selectPlayer) }
                .fold(alpha) { a, m ->
                    max(a, alphabeta(depth - 1, game.move(m), selectPlayer, a, beta))
                        .also { if (it >= beta) return@fold it }
                }
            else -> game.availableMoves
                .sortedBy { m -> alphabeta(0, game.move(m), selectPlayer) }
                .fold(beta) { b, m ->
                    min(b, alphabeta(depth - 1, game.move(m), selectPlayer, alpha, b))
                        .also { if (it <= alpha) return@fold it }
                }
        }
    }
}

