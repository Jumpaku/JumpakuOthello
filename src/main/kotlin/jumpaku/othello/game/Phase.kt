package jumpaku.othello.game


sealed class Phase(val board: Board) {

    companion object {

        fun init(
            player: Disc = Disc.Dark,
            board: Board = Board(0x0000000810000000uL, 0x0000001008000000uL)
        ): Phase = InProgress(player, board)

        fun of(board: Board, player: Disc) : Phase =
            if (Disc.values().all { board.availablePos(it).isEmpty() }) Completed(board)
            else InProgress(player, board)
    }

    class InProgress(val player: Disc, board: Board) : Phase(board) {

        init {
            check(board.availablePos(Disc.Dark).isNotEmpty() || board.availablePos(Disc.Light).isNotEmpty())
        }
    }

    class Completed(board: Board) : Phase(board) {

        val darkCount: Int = board.count(Disc.Dark)

        val lightCount: Int = board.count(Disc.Light)

        val winner: Disc? = when {
            darkCount > lightCount -> Disc.Dark
            darkCount < lightCount -> Disc.Light
            else -> null
        }

        val loser: Disc? = winner?.reverse()

        val isTie: Boolean = darkCount == lightCount

        val isSettled: Boolean = !isTie
    }

    sealed class State {

        class WaitingMove(val player: Disc) : State()

        data class Completed(val darkCount: Int, val lightCount: Int): State() {

            val winner: Disc? = when {
                darkCount > lightCount -> Disc.Dark
                darkCount < lightCount -> Disc.Light
                else -> null
            }

            val loser: Disc? = winner?.reverse()

            val isTie: Boolean = darkCount == lightCount

            val isSettled: Boolean = !isTie
        }
    }

    fun move(move: Move): Phase {
        require(this is InProgress)
        require(move in availableMoves) {"not available $move"}
        return when(move) {
            is Move.Pass -> InProgress(player.reverse(), board)
            is Move.Place -> {
                val placed = board.place(move.pos, player)
                if (Disc.values().all { placed.availablePos(it).isEmpty() }) Completed(placed)
                else InProgress(player.reverse(), placed)
            }
        }
    }

    val availableMoves: List<Move> by lazy {
        when (this) {
            is InProgress -> {
                val moves = board.availablePos(player).map { Move.Place(it) }
                if (moves.isNotEmpty()) moves else listOf(Move.Pass)
            }
            is Completed -> emptyList()
        }
    }
    /**
     * the number of existing discs without initial 4 discs
     */
    val progress: Int = board.count(Disc.Dark) + board.count(Disc.Light) - 4
}
