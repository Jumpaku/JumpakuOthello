package jumpaku.othello.selectors

import jumpaku.othello.game.Phase
import jumpaku.othello.game.Move


interface Selector {

    fun select(phase: Phase): Move
}

