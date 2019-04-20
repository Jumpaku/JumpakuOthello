package jumpaku.othello.game

import jumpaku.othello.selectors.Selector

data class Game(val history: List<Move> = emptyList(), val phases: List<Phase> = listOf(Phase.init())) {

    val phase: Phase = phases.last()

    fun update(selector: Selector): Game = when (phase) {
        is Phase.Completed -> this
        is Phase.InProgress -> selector.select(phase).let {
            copy(history = history + it, phases = phases + phase.move(it))
        }
    }

    fun undo(): Game = copy(phases = phases.dropLast(1))
}