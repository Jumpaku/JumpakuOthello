package jumpaku

import jumpaku.commons.control.orDefault
import jumpaku.othello.game.*
import jumpaku.othello.selectors.*
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.StringReader
import kotlin.concurrent.thread


fun printBoard(board: Board) {
    val top = (1..8).joinToString(" | ", "   | ", " |")
    println(top)
    val sep = top.map { "-" }.joinToString("")
    (1..8).forEach { i ->
        println(sep)
        print(" $i |")
        (1..8).forEach { j ->
            print(" ${board[Pos(i - 1, j - 1)]?.let { if (it == Disc.Light) "●" else "○" } ?: " "} |")
        }
        println()
    }
    println(sep)
}

fun printBoardFixed(board: Board) {
    val fixedD = board.fixedDiscs(Disc.Dark)
    val fixedL = board.fixedDiscs(Disc.Light)
    val top = (1..8).joinToString(" | ", "   | ", " |")
    println(top)
    val sep = top.map { "-" }.joinToString("")
    (1..8).forEach { i ->
        println(sep)
        print(" $i |")
        (1..8).forEach { j ->
            print(" ${board[Pos(i - 1, j - 1)]?.let {
                when {
                    it == Disc.Dark && Pos(i-1, j-1) in fixedD -> "*"
                    it == Disc.Dark -> "○"
                    it == Disc.Light && Pos(i-1, j-1) in fixedL-> "@"
                    else -> "●"
                }
            } ?: " "} |")
        }
        println()
    }
    println(sep)
}

var timePlace = 0.0
var timeBuildBoard = 0.0
var timeEvaluate = 0.0
var timeEvalPlaced = 0.0
var timeAvailable = 0.0
fun play(darkSelector: Selector, lightSelector: Selector, printsBoard: Boolean = false): Game.Result {
    var game = Game()
    while (game.state is Game.State.WaitingMove) {
        if (printsBoard) {
            printBoard(game.board)
            //printBoardFixed(game.board)
        }
        val p = (game.state as Game.State.WaitingMove).player
        val move = (if (p == Disc.Dark) darkSelector else lightSelector).select(game)
        if (printsBoard) println((move as? Move.Place)?.let { "${it.pos.col+1}${it.pos.row+1}" })
        game = game.move(move)
        if (printsBoard) {
            println()
            println("D : pe = ${PatternEvaluator().evaluate(game, Disc.Dark)},pl = ${Evaluator().evaluatePlaced(game, Disc.Dark)},av = ${Evaluator().evaluateAvailable(game, Disc.Dark)}")
            println("L : pe = ${PatternEvaluator().evaluate(game, Disc.Light)},pl = ${Evaluator().evaluatePlaced(game, Disc.Light)},av = ${Evaluator().evaluateAvailable(game, Disc.Light)}")
            //println("L : pe = ${PatternEvaluator().evaluate(game, Disc.Light)}")
        }
    }
    if (printsBoard) {
        printBoard(game.board)
        //printBoardFixed(game.board)
    }
    return (game.state as Game.State.Completed).result
}
fun main() {
    val h = "f5f6e6f4g5e7e3f3c5d3g4g3g6c3d7c6c4d6b5b3b4h4c7d8h5h3f8f7h2a6a5h7f2c8b6a4b7e2g2f1b8a8a7h1h6g7g1d2c1c2d1e1h8g8e8b1a1b2a3a2"
        .replace("a", "1")
        .replace("b", "2")
        .replace("c", "3")
        .replace("d", "4")
        .replace("e", "5")
        .replace("f", "6")
        .replace("g", "7")
        .replace("h", "8")
    fun compute(seed: Int) {
        val randomSelector = RandomSelector(seed)
        val aiSelector = AiSelector(1089)
        val resultL = play(randomSelector, aiSelector)
        println(resultL)
        println("$seed\tL\t${(resultL as? Game.Result.WinLose)?.winner?.first == Disc.Light}")
        val resultD = play(aiSelector, randomSelector)
        println(resultD)
        println("$seed\tD\t${(resultD as? Game.Result.WinLose)?.winner?.first == Disc.Dark}")
    }
    compute(500)
    //listOf(101..125, 126..150, 151..175, 176..200, 1..25, 26..50, 51..75, 76..100)
    //    .map { thread { it.forEach(::compute) } }
    //    .forEach { it.join() }
    //val b0 = ByteArrayInputStream(h.windowed(2, 2).joinToString("\n", postfix = "\n").toByteArray())
    //val b1 = ByteArrayInputStream(h.windowed(2, 2).joinToString("\n", postfix = "\n").toByteArray())
    //println(play(
    //    InputSelector(b0), InputSelector(b1), true))
    //    AiSelector(1089), InputSelector(System.`in`), true))
}

