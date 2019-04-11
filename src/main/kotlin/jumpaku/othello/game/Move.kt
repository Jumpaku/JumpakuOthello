package jumpaku.othello.game

sealed class Move {

    object Pass : Move()

    data class Place(val pos: Pos) : Move()
}