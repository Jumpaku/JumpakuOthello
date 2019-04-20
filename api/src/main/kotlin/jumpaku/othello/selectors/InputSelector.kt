package jumpaku.othello.selectors

import jumpaku.commons.control.result
import jumpaku.othello.game.Phase
import jumpaku.othello.game.Move
import jumpaku.othello.game.Pos
import java.io.InputStream
import java.util.*

class InputSelector(input: InputStream) : Selector {

    private val scanner = Scanner(input)

    override fun select(phase: Phase): Move {
        require(phase is Phase.InProgress)
        val ms = phase.availableMoves
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