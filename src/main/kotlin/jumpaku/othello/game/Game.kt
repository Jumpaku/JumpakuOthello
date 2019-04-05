package jumpaku.othello.game


sealed class Move {

    object Pass : Move()

    data class Place(val pos: Pos) : Move()
}

class Game(
    val board: Board = Board(
        mapOf(
            Pos(3, 3) to Disc.Light,
            Pos(4, 4) to Disc.Light,
            Pos(3, 4) to Disc.Dark,
            Pos(4, 3) to Disc.Dark
        )
    ),
    val state: State = State.WaitingMove(Disc.Dark),
    val history: List<Move> = emptyList()
) {

    sealed class State {

        class WaitingMove(val player: Disc) : State()

        class Completed(val result: Result) : State()

        fun next(board: Board) : State {
            val player = (this as WaitingMove).player
            val opponent = player.reverse()
            val nD = board.flatMap { it.value.filter { it == Disc.Dark } }.size
            val nL = board.flatMap { it.value.filter { it == Disc.Light } }.size
            return when {
                listOf(player, opponent).any { board.availablePositions(it).isNotEmpty() } -> WaitingMove(opponent)
                nD > nL -> Completed(Result.WinLose(Disc.Dark to nD, Disc.Light to nL))
                nD < nL -> Completed(Result.WinLose(Disc.Light to nL, Disc.Dark to nD))
                else -> Completed(Result.Tie(nD))
            }
        }
    }

    sealed class Result {

        data class Tie(val count: Int) : Result()

        data class WinLose(val winner: Pair<Disc, Int>, val loser: Pair<Disc, Int>) : Result()
    }

    fun move(move: Move): Game {
        require(state is State.WaitingMove)
        return when(move) {
            is Move.Pass -> Game(board, state.next(board), history + move)
            is Move.Place -> {
                val pos = move.pos
                require(move in availableMoves) {"not available $pos"}
                val b = board.place(pos, state.player)
                Game(b, state.next(b), history + move)
            }
        }
    }

    val availableMoves: List<Move> = when(state){
        is State.WaitingMove -> {
            val moves = board.availablePositions(state.player).map { Move.Place(it) }
            if (moves.isNotEmpty()) moves else listOf(Move.Pass)
        }
        is State.Completed -> emptyList()
    }

    val progress: Int = board.count { it.value.isDefined } - 4

    fun undo(n: Int): Game {
        require(history.size >= n)
        return history.dropLast(n).fold(Game()) { g, m -> g.move(m) }
    }
}
