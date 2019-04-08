package jumpaku.othello.game

import jumpaku.commons.control.*

private val boardKeys: Set<Pos> = (0..7).flatMap { i -> (0..7).map { j -> Pos(i, j) } }.toSet()

class Board(discs: Map<Pos, Disc>): Map<Pos, Option<Disc>> {
    private val discs: Map<Pos, Disc> = discs.toMap()
    override val keys: Set<Pos> = boardKeys
    override val size: Int = 64
    override val values: Collection<Option<Disc>> = keys.map { discs[it]?.let(::Some) ?: None }
    override fun isEmpty(): Boolean = false
    override fun containsKey(key: Pos): Boolean = true
    override fun containsValue(value: Option<Disc>): Boolean = value in values
    override fun get(key: Pos): Option<Disc> = discs[key]?.let(::Some) ?: None
    override val entries: Set<Map.Entry<Pos, Option<Disc>>> = boardKeys
        .map {
            object : Map.Entry<Pos, Option<Disc>> {
                override val key: Pos = it
                override val value: Option<Disc> = (discs[it]?.let(::Some) ?: None)
            }
        }.toSet()

    fun iterator(pos: Pos, direction: Pos.Vec): Iterator<Pos> = object : Iterator<Pos> {
        var currentPos = some(pos)
        override fun hasNext(): Boolean = currentPos.isDefined
        override fun next(): Pos {
            val r = currentPos
            currentPos = currentPos.flatMap { it + direction }
            return r.orThrow { NoSuchElementException() }
        }

    }

    private fun reversedCounts(pos: Pos, disc: Disc): Map<Pos.Vec, Int> {
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

    private fun isAvailable(pos: Pos, disc: Disc): Boolean = reversedCounts(pos, disc).any { it.value > 0 }

    private val availablePositionsCacheD: Set<Pos> by lazy {
        filter { it.value.isEmpty && isAvailable(it.key, Disc.Dark) }.keys
    }
    private val availablePositionsCacheL: Set<Pos> by lazy {
        filter { it.value.isEmpty && isAvailable(it.key, Disc.Light) }.keys
    }

    fun availablePositions(disc: Disc): Set<Pos> = when (disc) {
        Disc.Dark -> availablePositionsCacheD
        Disc.Light -> availablePositionsCacheL
    }

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


    fun findFixed(disc: Disc): Set<Pos> {
        // checks corner pos
        val isFixed = mapValues { false }.toMutableMap()
        val c00 = Pos(0, 0)
        val c07 = Pos(0, 7)
        val c70 = Pos(7, 0)
        val c77 = Pos(7, 7)
        val corners = setOf(c00, c07, c70, c77)
        corners.forEach { c -> get(c).forEach { isFixed[c] = it == disc } }

        // checks edge pos
        val vD = Pos.Vec(1, 0)
        val vR = Pos.Vec(0, 1)
        val vL = Pos.Vec(0, -1)
        val vU = Pos.Vec(-1, 0)
        listOf(c00 to vD, c00 to vR, c07 to vD, c07 to vL, c70 to vU, c70 to vR, c77 to vU, c77 to vL).forEach { (c, d) ->
            iterator(c, d).asSequence().zipWithNext().forEach { (prev, next) ->
                if (isFixed[prev]!!) get(next).forEach { isFixed[next] = isFixed[next]!! || it == disc }
            }
        }
        listOf<(Int) -> Pos>({ Pos(it, 0) }, { Pos(it, 7) }, { Pos(0, it) }, { Pos(7, it) }).map((0..7)::map)
            .forEach { edge ->
                if (edge.all { get(it).isDefined }) edge.forEach { pos ->
                    get(pos).forEach { isFixed[pos] = isFixed[pos]!! || it == disc }
                }
            }

        // checks inner pos
        val v = setOf(Pos.Vec(-1, 0), Pos.Vec(1, 0))
        val h = setOf(Pos.Vec(0, -1), Pos.Vec(0, 1))
        val s = setOf(Pos.Vec(-1, 1), Pos.Vec(1, -1))
        val b = setOf(Pos.Vec(-1, -1), Pos.Vec(1, 1))
        val dirs = listOf(v, h, s, b)
        for (i in 1..3) {
            val lt = Pos(i, i)
            val rt = Pos(i, 7 - i)
            val lb = Pos(7 - i, i)
            val rb = Pos(7 - i, 7 - i)
            listOf(lt to vD, lt to vR, rt to vD, rt to vL, lb to vU, lb to vR, rb to vU, rb to vL).forEach { (x, d) ->
                iterator(x, d).asSequence()
                    .filter { pos -> dirs.all { dir -> dir.any { v -> (pos + v).map { isFixed[it]!! }.orDefault(true) } } }
                    .forEach { pos -> get(pos).forEach { isFixed[pos] = isFixed[pos]!! || it == disc } }
            }
        }
        return keys.filter { isFixed[it]!! }.toSet()
    }

    fun normalize(normalize: Pos.Normalize): Board = Board(discs.mapKeys { (pos, _) -> normalize(pos) })

}
