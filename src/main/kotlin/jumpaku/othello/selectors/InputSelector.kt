package jumpaku.othello.selectors

import jumpaku.commons.control.result
import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import jumpaku.othello.game.Pos
import java.io.InputStream
import java.util.*

class InputSelector(input: InputStream) : Selector {

    private val scanner = Scanner(input)

    override fun select(game: Game): Move {
        require(game.state is Game.State.WaitingMove)
        val ms = game.availableMoves()
        if (ms.any { it is Move.Pass }) return Move.Pass
        while (true) {
            val l = scanner.nextLine()
            if (l.length != 2) continue
            return result {
                val r = l.drop(1).toInt() - 1
                val c = l.take(1).toInt() - 1

                Move.Place(Pos(r, c))
            }.value().filter { it.pos in ms.map { (it as Move.Place).pos } }.orNull() ?: continue
        }
    }
}