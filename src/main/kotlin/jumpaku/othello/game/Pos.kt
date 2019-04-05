package jumpaku.othello.game

import jumpaku.commons.control.Option
import jumpaku.commons.control.result

data class Pos(val row: Int, val col: Int) {

    data class Vec(val i: Int, val j: Int)

    infix operator fun plus(v: Vec): Option<Pos> = result {
        Pos(
            row + v.i,
            col + v.j
        )
    }.value()

    companion object {

        fun directions(): Set<Vec> = setOf(
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

    init {
        require(row in 0..7 && col in 0..7) { "invalid position ($row, $col)"}
    }
}