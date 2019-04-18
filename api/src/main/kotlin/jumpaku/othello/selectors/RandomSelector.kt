package jumpaku.othello.selectors

import jumpaku.othello.game.Phase
import jumpaku.othello.game.Move
import kotlin.random.Random

class RandomSelector(val seed: Int = 1089) : Selector {

    override fun select(phase: Phase): Move {
        require(phase is Phase.InProgress)
        return phase.availableMoves.shuffled(Random(seed)).first()
    }
}