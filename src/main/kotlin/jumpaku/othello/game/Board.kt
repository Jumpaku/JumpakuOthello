package jumpaku.othello.game

import java.util.*

@ExperimentalUnsignedTypes
class Board(val darkBits: ULong = 0uL, val lightBits: ULong = 0uL) {

    constructor(discs: Map<Pos, Disc>) : this(
        discs.filter { it.value == Disc.Dark }.map { it.key.bits }.reduce(ULong::or),
        discs.filter { it.value == Disc.Light }.map { it.key.bits }.reduce(ULong::or)
    )


    operator fun contains(key: Pos): Boolean = key.bits and (darkBits or lightBits) != 0uL

    operator fun get(key: Pos): Disc? = when {
        darkBits and key.bits != 0uL -> Disc.Dark
        lightBits and key.bits != 0uL -> Disc.Light
        else -> null
    }

    fun availablePos(disc: Disc): Set<Pos> {
        val opponentBoard = if (disc == Disc.Light) darkBits else lightBits
        val playerBoard = if (disc == Disc.Dark) darkBits else lightBits

        val oMaskV = opponentBoard and 0x00FFFFFFFFFFFF00uL
        val initN = oMaskV and (playerBoard shl 8)
        val candN = (1..5).fold(initN) { t, _ -> t or (oMaskV and (t shl 8)) } shl 8
        val initS = oMaskV and (playerBoard shr 8)
        val candS = (1..5).fold(initS) { t, _ -> t or (oMaskV and (t shr 8)) } shr 8

        val oMaskH = opponentBoard and 0x7e7e7e7e7e7e7e7euL
        val initW = oMaskH and (playerBoard shl 1)
        val candW = (1..5).fold(initW) { t, _ -> t or (oMaskH and (t shl 1)) } shl 1
        val initE = oMaskH and (playerBoard shr 1)
        val candE = (1..5).fold(initE) { t, _ -> t or (oMaskH and (t shr 1)) } shr 1

        val oMask = opponentBoard and 0x007e7e7e7e7e7e00uL
        val initNE = oMask and (playerBoard shl 7)
        val candNE = (1..5).fold(initNE) { t, _ -> t or (oMask and (t shl 7)) } shl 7
        val initNW = oMask and (playerBoard shl 9)
        val candNW = (1..5).fold(initNW) { t, _ -> t or (oMask and (t shl 9)) } shl 9
        val initSE = oMask and (playerBoard shr 9)
        val candSE = (1..5).fold(initSE) { t, _ -> t or (oMask and (t shr 9)) } shr 9
        val initSW = oMask and (playerBoard shr 7)
        val candSW = (1..5).fold(initSW) { t, _ -> t or (oMask and (t shr 7)) } shr 7

        val available =
            (candN or candS or candE or candW or candNE or candNW or candSE or candSW) and (darkBits or lightBits).inv()
        return (0..63).mapNotNull {
            if (available and (1uL shl it) != 0uL) Pos(it / 8, it % 8) else null
        }.toSet()
    }

    fun place(pos: Pos, disc: Disc): Board {
        require(pos in availablePos(disc))
        val placed = pos.bits
        val playerBoard = if (disc == Disc.Dark) darkBits else lightBits
        val opponentBoard = if (disc == Disc.Light) darkBits else lightBits
        fun f(p: ULong, v: Pos.Direction): ULong = when (v) {
            Pos.Direction.N -> (p shl 8) and 0xffffffffffffff00uL
            Pos.Direction.NE -> (p shl 7) and 0x7f7f7f7f7f7f7f00uL
            Pos.Direction.E -> (p shr 1) and 0x7f7f7f7f7f7f7f7fuL
            Pos.Direction.SE -> (p shr 9) and 0x007f7f7f7f7f7f7fuL
            Pos.Direction.S -> (p shr 8) and 0x00ffffffffffffffuL
            Pos.Direction.SW -> (p shr 7) and 0x00fefefefefefefeuL
            Pos.Direction.W -> (p shl 1) and 0xfefefefefefefefeuL
            Pos.Direction.NW -> (p shl 9) and 0xfefefefefefefe00uL
        }

        val reversed = Pos.Direction.values().map { v ->
            var rev = 0uL
            var mask = f(placed, v)
            while ((mask != 0uL) && ((mask and opponentBoard) != 0uL)) {
                rev = rev or mask
                mask = f(mask, v)
            }
            if (mask and playerBoard != 0uL) rev else 0uL
        }.reduce(ULong::or)
        val updatedPlayerBoard = playerBoard or reversed or placed
        val updatedOpponentBoard = opponentBoard xor reversed
        System.nanoTime()
        return if (disc == Disc.Dark) Board(darkBits = updatedPlayerBoard, lightBits = updatedOpponentBoard)
        else Board(darkBits = updatedOpponentBoard, lightBits = updatedPlayerBoard)
    }

    fun fixedDiscs(disc: Disc): Set<Pos> {
        val board = if (disc == Disc.Light) lightBits else darkBits
        val initN = board and 0x00000000000000ffuL
        val fixedOnN = (1..7).fold(initN) { mask, _ -> board and (mask or ((0x00ffffffffffffffuL and mask) shl 8)) }
        val initS = board and 0xff00000000000000uL
        val fixedOnS = (1..7).fold(initS) { mask, _ -> board and (mask or ((0xffffffffffffff00uL and mask) shr 8)) }
        val initW = board and 0x0101010101010101uL
        val fixedOnW = (1..7).fold(initW) { mask, _ -> board and (mask or ((0x7f7f7f7f7f7f7f7fuL and mask) shl 1)) }
        val initE = board and 0x8080808080808080uL
        val fixedOnE = (1..7).fold(initE) { mask, _ -> board and (mask or ((0xfefefefefefefefeuL and mask) shr 1)) }
        val initNE = board and 0x80808080808080ffuL
        val fixedOnNE = (1..7).fold(initNE) { mask, _ -> board and (mask or ((0x00fefefefefefefeuL and mask) shl 7)) }
        val initNW = board and 0x01010101010101ffuL
        val fixedOnNW = (1..7).fold(initNW) { mask, _ -> board and (mask or ((0x007f7f7f7f7f7f7fuL and mask) shl 9)) }
        val initSE = board and 0xff80808080808080uL
        val fixedOnSE = (1..7).fold(initSE) { mask, _ -> board and (mask or ((0xfefefefefefefe00uL and mask) shr 9)) }
        val initSW = board and 0xff01010101010101uL
        val fixedOnSW = (1..7).fold(initSW) { mask, _ -> board and (mask or ((0x7f7f7f7f7f7f7f00uL and mask) shr 7)) }

        val fixedOnEdge =
            sequenceOf(0xff00000000000000uL, 0x00000000000000ffuL, 0x0101010101010101uL, 0x8080808080808080uL)
                .filter { (darkBits or lightBits) and it == it }
                .map { board and it }
                .fold(0uL, ULong::or)

        val fixed =
            fixedOnEdge or ((fixedOnN or fixedOnS) and (fixedOnW or fixedOnE) and (fixedOnNW or fixedOnSE) and (fixedOnNE or fixedOnSW))
        return (0..63).mapNotNull {
            if (fixed and (1uL shl it) != 0uL) Pos(it / 8, it % 8) else null
        }.toSet()

    }

    fun normalize(normalizer: Pos.Normalizer): Board =
        Board(Pos.enumerate.mapNotNull { p -> get(p)?.let { normalizer(p) to it } }.toMap())


    private val darkCount = countBits(darkBits)

    private val lightCount = countBits(lightBits)

    fun count(disc: Disc): Int = if (disc == Disc.Dark) darkCount else lightCount

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Board -> false
        darkBits != other.darkBits -> false
        lightBits != other.lightBits -> false
        else -> true
    }

    override fun hashCode(): Int = Objects.hash(darkBits, lightBits)
}
