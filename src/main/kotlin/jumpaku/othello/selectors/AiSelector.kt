package jumpaku.othello.selectors

import jumpaku.*
import jumpaku.othello.game.Disc
import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

var c = 0
var ce = 0
var winningCount = 0
class AiSelector(seed: Int) : Selector {

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
        return when (game.progress) {
            0 -> 1
            in 1..5 -> 3
            in 6..16 -> estimateDepth(300, 3)
            in 17..26 -> 2
            in 27..30 -> estimateDepth(300, 3)
            in 31..41 -> estimateDepth(500, 3)
            in 42..44 -> estimateDepth(1000, 3)
            in 45..47 -> estimateDepth(1000, 4)
            in 48..52 -> estimateDepth(1000, 5)
            else -> 8
        }
    }

    override fun select(game: Game): Move {
        require(game.state is Game.State.WaitingMove)
        println(game.progress)
        val ms = game.availableMoves
        if (ms.size == 1) return ms.first()
        if (game.progress < 2) return ms.shuffled(random).first()
        val player = game.state.player
        val depth = computeDepth(game)
        if (game.progress > 43) {
            val ws = System.nanoTime()
            val winning = ms.find { isWinningGame(100, game.move(it), player) }
                //?: ms.maxBy { minmax(8, game.move(it), player) }!!
            println(", $winningCount ${(System.nanoTime() - ws)*1e-9}")
            winningCount=0
            if (winning != null) return winning
        }
        val s = System.nanoTime()
        print(depth)
        val m = ms.maxBy { minmax(depth, game.move(it), player) }!!
        println(", $c, ${(System.nanoTime() - s)*1e-9}")
        c=0

        println("time------Place $timePlace")
        timePlace = 0.0
        println("time-BuildBoard $timeBuildBoard")
        timeBuildBoard = 0.0
        println("time---Evaluate $timeEvaluate")
        timeEvaluate = 0.0
        println("time----timeEvalPlaced $timeEvalPlaced")
        timeEvalPlaced = 0.0
        println("time-----timeAvailable $timeAvailable")
        timeAvailable = 0.0
        println("time---------------timeAvailablePositions $timeAvailablePositions")
        timeAvailablePositions = 0.0
        println("time------------------------timeFindFixed $timeFindFixed")
        timeFindFixed = 0.0
        println("time---------------------timeSumAvailable $timeSumAvailable")
        timeSumAvailable = 0.0
        println("time------------------------timeSumPlaced $timeSumPlaced")
        timeSumPlaced = 0.0
        return m
    }


    fun isWinningGame(depth: Int, game: Game, selectPlayer: Disc): Boolean {
        if (depth < 0) winningCount++
        return depth >= 0 && when (game.state) {
            is Game.State.Completed -> {
                winningCount++
                (game.state.result as? Game.Result.WinLose)?.winner?.first == selectPlayer
            }
            is Game.State.WaitingMove -> when (selectPlayer) {
                game.state.player -> game.availableMoves.any { isWinningGame(depth - 1, game.move(it), selectPlayer) }
                else -> game.availableMoves.all { isWinningGame(depth - 1, game.move(it), selectPlayer) }
            }
        }
    }

    private val random = Random(1089)

    fun minmax(depth: Int, game: Game, selectPlayer: Disc): Double = when (game.state) {
        is Game.State.Completed -> when {
            game.state.result is Game.Result.WinLose && selectPlayer == game.state.result.winner.first -> Double.MAX_VALUE
            game.state.result is Game.Result.WinLose && selectPlayer == game.state.result.loser.first -> Double.MIN_VALUE
            else -> 0.0
        }
        is Game.State.WaitingMove ->
            if (depth == 0) Evaluator().evaluate(game, selectPlayer)
            else game.availableMoves.shuffled(random)
                .map { minmax(depth - 1, game.move(it), selectPlayer) }
                .run { if (game.state.player == selectPlayer) max() else min() }!!
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

