package jumpaku.othello.selectors

import jumpaku.othello.game.Game
import jumpaku.othello.game.Move

interface Selector {
    fun select(game: Game): Move
}

