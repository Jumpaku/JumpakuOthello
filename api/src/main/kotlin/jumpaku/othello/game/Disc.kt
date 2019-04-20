package jumpaku.othello.game

enum class Disc {

    Dark,
    Light;

    fun reverse(): Disc = if (this == Dark) Light else Dark
}