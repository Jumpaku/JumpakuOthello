package jumpaku.othello.game

import jumpaku.commons.control.Option
import jumpaku.commons.control.result

data class Pos(val row: Int, val col: Int) {

    class Normalize(pos: Pos) {
        private val r = pos.row
        private val c = pos.col
        operator fun invoke(p: Pos): Pos {
            fun rotate(p: Pos): Pos = Pos(7 - p.col, p.row)
            fun reflect(p: Pos): Pos = Pos(p.col, p.row)
            val labelMap = arrayOf(
                intArrayOf(0, 0, 0, 0, 3, 3, 3, 2),
                intArrayOf(1, 0, 0, 0, 3, 3, 2, 2),
                intArrayOf(1, 1, 0, 0, 3, 2, 2, 2),
                intArrayOf(1, 1, 1, 0, 2, 2, 2, 2),
                intArrayOf(6, 6, 6, 6, 4, 5, 5, 5),
                intArrayOf(6, 6, 6, 7, 4, 4, 5, 5),
                intArrayOf(6, 6, 7, 7, 4, 4, 4, 5),
                intArrayOf(6, 7, 7, 7, 4, 4, 4, 4)
            )
            return listOf(
                p,
                p.let(::reflect),
                p.let(::rotate),
                p.let(::rotate).let(::reflect),
                p.let(::rotate).let(::rotate),
                p.let(::rotate).let(::rotate).let(::reflect),
                p.let(::rotate).let(::rotate).let(::rotate),
                p.let(::rotate).let(::rotate).let(::rotate).let(::reflect)
            )[labelMap[r][c]]
        }
    }

    data class Vec(val i: Int, val j: Int)

    infix operator fun plus(v: Vec): Option<Pos> = result { Pos(row + v.i, col + v.j) }.value()

    init {
        require(row in 0..7 && col in 0..7) { "invalid position ($row, $col)"}
    }

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

        fun normalizers(): Set<Normalize> = listOf(
            Pos(0, 3),
            Pos(3, 0),
            Pos(3, 7),
            Pos(0, 4),
            Pos(7, 4),
            Pos(4, 7),
            Pos(4, 0),
            Pos(7, 3)
        ).map { Normalize(it) }.toSet()
    }
}