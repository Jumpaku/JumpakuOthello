package jumpaku

import jumpaku.commons.control.orDefault
import jumpaku.othello.game.*
import jumpaku.othello.selectors.*
import kotlin.concurrent.thread


fun printBoard(board: Board) {
    val top = (1..8).joinToString(" | ", "   | ", " |")
    println(top)
    val sep = top.map { "-" }.joinToString("")
    (1..8).forEach { i ->
        println(sep)
        print(" $i |")
        (1..8).forEach { j ->
            print(" ${board[Pos(i - 1, j - 1)].map { if (it == Disc.Light) "●" else "○" }.orDefault(" ")} |")
        }
        println()
    }
    println(sep)
}

fun play(darkSelector: Selector, lightSelector: Selector, printsBoard: Boolean = false): Game.Result {
    var game = Game()
    while (game.state is Game.State.WaitingMove) {
        if (printsBoard) printBoard(game.board)
        val p = (game.state as Game.State.WaitingMove).player
        val move = (if (p == Disc.Dark) darkSelector else lightSelector).select(game)
        if (printsBoard) println((move as? Move.Place)?.let { "${it.pos.col+1}${it.pos.row+1}" })
        game = game.move(move)
        if (printsBoard) println()
    }
    if (printsBoard) printBoard(game.board)
    return (game.state as Game.State.Completed).result
}
fun main() {
    //"F5D6C5F6C4F4E6D7E7C6F7D8C8E8G5B8E3F8B6B5A6A4A5A7C7B4G6H6H7B3D3C3C2C1H5D2E2F1B1D1B2A3E1H4H3G4G3F3G1F2B7H2H1G7"
    fun compute(seed: Int) {
        val randomSelector = RandomSelector(seed)
        val aiSelector = AiSelector()
        val resultL = play(randomSelector, aiSelector)
        println("$seed\tL\t${(resultL as? Game.Result.WinLose)?.winner?.first == Disc.Light}")
        val resultD = play(aiSelector, randomSelector)
        println("$seed\tD\t${(resultD as? Game.Result.WinLose)?.winner?.first == Disc.Dark}")
    }
    val ts = listOf(1..25, 26..50, 51..75, 76..100).map { thread { it.forEach(::compute) } }
    ts.forEach { it.join() }
    println("placed avg : ${placedValueSum/ c}, available avg : ${availableValueSum/ c}")
    //println(play(InputSelector(System.`in`), AiSelector(), true))
}

