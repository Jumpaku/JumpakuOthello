package jumpaku.othello.selectors

import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import kotlin.random.Random

class RandomSelector(val seed: Int = 1089) : Selector {

    override fun select(game: Game): Move {
        require(game.state is Game.State.WaitingMove)
        return game.availableMoves().shuffled(Random(seed)).first()
    }
}