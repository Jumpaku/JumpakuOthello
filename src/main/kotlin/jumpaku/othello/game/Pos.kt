package jumpaku.othello.game

data class Pos(val row: Int, val col: Int) {

    enum class Normalizer(val f: (Pos) -> Pos) {
        I({ it }),
        Ref({ reflect(it) }),
        Rot({ rotate(it)}),
        RotRef({ reflect(rotate(it)) }),
        Rot2({ rotate(rotate(it)) }),
        Rot2Ref({ reflect(rotate(rotate(it))) }),
        Rot3({ rotate(rotate(rotate(it))) }),
        Rot3Ref({ reflect(rotate(rotate(rotate(it)))) }),
        ;
        operator fun invoke(p: Pos): Pos = f(p)

        companion object {

            private fun rotate(p: Pos): Pos = Pos(7 - p.col, p.row)

            private fun reflect(p: Pos): Pos = Pos(p.col, p.row)
        }
    }

    enum class Direction { N, NE, E, SE, S, SW, W, NW }

    init {
        require(row in 0..7 && col in 0..7) { "invalid position ($row, $col)" }
    }

    val bits: ULong = 1uL shl (row * 8 + col)

    companion object {

        val enumerate: Set<Pos> = (0..7).flatMap { i -> (0..7).map { j -> Pos(i, j) } }.toSet()
    }
}
