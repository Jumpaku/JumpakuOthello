package jumpaku.othello.game

import jumpaku.commons.control.Option
import jumpaku.commons.control.optionWhen
import jumpaku.commons.control.orDefault
import jumpaku.commons.control.some

class Board(discs: Map<Pos, Disc>): AbstractMap<Pos, Option<Disc>>()  {

    override val entries: Set<Map.Entry<Pos, Option<Disc>>> = (0..7).flatMap { i -> (0..7).map { j ->
        Pos(
            i,
            j
        )
    } }
        .map { it to optionWhen(it in discs) { discs.getValue(it) } }
        .toMap().entries

    override fun get(key: Pos): Option<Disc> = super.get(key)!!

    fun reversedCounts(pos: Pos, disc: Disc): Map<Pos.Vec, Int> {
        require(get(pos).isEmpty)
        val opponentsDisc = disc.reverse()
        return Pos.directions().map { dir ->
            val s = sequence {
                var p = some(pos)
                while (p.flatMap { (it + dir).flatMap { get(it).filter { it == opponentsDisc } } }.isDefined) {
                    p = p.flatMap { it + dir }
                    yield(p)
                }
            }
            val p = s.lastOrNull() ?: return@map dir to 0
            val n = s.count()
            dir to p.flatMap { (it + dir).flatMap { get(it).filter { it == disc } } }.map { n }.orDefault(0)
        }.toMap()
    }

    fun isAvailable(pos: Pos, disc: Disc): Boolean = reversedCounts(pos, disc).map { it.value }.sum() > 0

    fun availablePositions(disc: Disc): Set<Pos> = filter { it.value.isEmpty && isAvailable(it.key, disc) }.keys

    fun place(pos: Pos, disc: Disc): Board {
        require(isAvailable(pos, disc))
        return Board(mutableMapOf<Pos, Disc>().apply {
            this@Board.entries.forEach { (p, d) -> d.forEach { put(p, it) } }
            reversedCounts(pos, disc).filterValues { it > 0 }.forEach { v, n ->
                var p = some(pos)
                for (i in 0..n) {
                    p.forEach { put(it, disc) }
                    p = p.flatMap { it + v }
                }
            }
        })
    }
}