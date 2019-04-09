package jumpaku.othello.game

import jumpaku.commons.control.Option
import jumpaku.commons.control.result

data class Pos(val row: Int, val col: Int) {

    @ExperimentalUnsignedTypes
    val bits: ULong = 1uL shl (row * 8 + col)

    enum class Normalize(val f: (Pos) -> Pos) {
        I({ it }),
        Ref(::reflect),
        Rot(::rotate),
        RotRef({ reflect(rotate(it)) }),
        Rot2({ rotate(rotate(it)) }),
        Rot2Ref({ reflect(rotate(rotate(it))) }),
        Rot3({ rotate(rotate(rotate(it))) }),
        Rot3Ref({ reflect(rotate(rotate(rotate(it)))) }),
        ;
        operator fun invoke(p: Pos): Pos = f(p)

        companion object {
            fun rotate(p: Pos): Pos = Pos(7 - p.col, p.row)
            fun reflect(p: Pos): Pos = Pos(p.col, p.row)

        }
    }

    data class Vec(val i: Int, val j: Int)

    infix operator fun plus(v: Vec): Option<Pos> = result { Pos(row + v.i, col + v.j) }.value()

    init {
        require(row in 0..7 && col in 0..7) { "invalid position ($row, $col)" }
    }

    companion object {

        val enumerate: Set<Pos> = (0..7).flatMap { i -> (0..7).map { j -> Pos(i, j) } }.toSet()

        val directions: Set<Vec> = setOf(
            Vec(1, 1),
            Vec(1, 0),
            Vec(1, -1),
            Vec(0, 1),
            Vec(0, -1),
            Vec(-1, 1),
            Vec(-1, 0),
            Vec(-1, -1)
        )
    }
}
