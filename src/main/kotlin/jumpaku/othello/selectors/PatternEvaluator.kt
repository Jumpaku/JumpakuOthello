package jumpaku.othello.selectors

import jumpaku.commons.control.orDefault
import jumpaku.othello.game.Board
import jumpaku.othello.game.Disc
import jumpaku.othello.game.Game
import jumpaku.othello.game.Pos

class PatternEvaluator {

    companion object {
        private val o = 'o'//Focusing
        private val x = 'x'
        private val w = '_'
        private val n = '\n'
    }

    fun evaluate(game: Game, focusTo: Disc): Double = when(game.state) {
        is Game.State.Completed -> 0.0
        is Game.State.WaitingMove -> Pos.normalizers().flatMap { normalizer ->
            val board = toString(game.board.normalize(normalizer), focusTo)
            val onMove = focusTo == game.state.player
            BoardPattern.values().map { it.evaluate(board, onMove) }
        }.sum()
    }

    fun toString(
        board: Board,
        playerDisc: Disc
    ): String = board
        .mapValues { it.value.map { if (it == playerDisc) o else x }.orDefault(w) }
        .run {
            (0..7).joinToString("$n") { i ->
                (0..7).joinToString("") { j ->
                    "${getValue(Pos(i, j))}"
                }
            }
        }

    enum class BoardPattern(val regex: Regex, val computeScore: (String, Boolean) -> Double) {

        Corner(Regex("^o.{7}(\n.*){7}$"), { s, c -> 20.0 * s.take(8).takeWhile { it == o }.length }),
        Mountain(Regex("^_o{6}_\n.(_|x).{4}(_|x).(\n.*){6}$"), { s, c -> 60.0 }),
        HalfSquare(Regex("^__o..(x|_)__\n._o.{3}_.(\n.*){6}$"), { s, c -> 20.0 }),
        Square(Regex("^__o.{2}o__\n._o.{2}o_.(\n.*){6}$"), { s, c -> 20.0 }),
        GoodA(Regex("^__o.{3}__\n._(x|_).{3}_.(\n.*){6}$"), { s, c -> 20.0 }),
        CheckCornerC(Regex("^_x+o+(x+.*|_x+o+.*)(\n.*){7}$"), { s, c -> 70.0 }),
        CheckCornerX(Regex("^_.{7}\n.x.*\n..(o.*(\n.*){5}|x.*\n.{3}(o.*(\n.*){4}|x.*\n.{4}(o.*(\n.*){3}|x.*\n.{5}(o.*(\n.*){2}|x.*\n.{6}(o.\n.*(o|x)|x.\n.*o)))))$"), { s, c -> 20.0 }),
        ExpectCorner1(Regex("^_x+_x+((_|o+x).*|.*o)(\n.*){7}$"), { s, c -> 10.0 }),
        ExpectCorner2(Regex("^_x+__o+((_|x+o).*|.*o)(\n.*){7}$"), { s, c -> 20.0 }),
        ExpectCorner3(Regex("^_x+_o+(x+o.*|.*o|.*__)(\n.*){7}$"), { s, c -> 20.0 }),
        Wing(Regex("^__x{5}_\n._x.*(\n.*){6}$"), { s, c -> 20.0 }),
        AttackWing(Regex("^__x{5}_\n.o(o|x).*\n.(o|x){2}.*(\n.*){5}$"), { s, c -> 40.0 }),
        Standoff(Regex("^_o+_x+__\n._.{4}_.(\n.*){6}$"), { s, c -> 20.0 }),
        AttackStandoff(Regex("^(_o_xxxx_\n.o.*x+.*.{4}|_oo_xxx_\n.o.*x+.*.{3}|_ooo_xx_\n.o.+x+.*..|_oooo_x_\n.o..+x+.*..)(\n.*){6}$"), { s, c ->
            10.0 * (1 + s.slice(1..6).count { it == o })
        }),
        SafeX(Regex("^_.*\n.o.*\n..o.*\n.{3}o.*\n.{4}o.*\n.{5}o.*\n.{6}(o.\n.*(o|_)|_.\n.*_)$"), { s, c ->
            when(s.slice(listOf(60, 70))) {
                "oo" -> 50.0
                "__" -> 30.0
                "o_" -> 10.0
                else -> error("")
            }
        }),
        CrossFour(Regex("^_.*\n._.*\n..o.*\n.{3}o.*\n.{4}o.*\n.{5}o.*\n.{6}_.\n.*_$"), { s, c -> 10.0 }),
        OneBlanks1(Regex("^_(o|x).*\n(o|x){2}.*(\n.*){6}$"), { s, c ->
            50.0 * scoreHelperBlankPattern(s.slice(setOf(1, 9, 10)), c)
        }),
        OneBlanks2(Regex("^(o|x)_(o|x).*\n(o|x){3}.*(\n.*){6}$"), { s, c ->
            (if (s[0] == o) 60.0 else 10.0) * scoreHelperBlankPattern(s.slice(setOf(2, 10, 11)), c)
        }),
        OneBlanks3(Regex("^(o|x){3}.*\n(o|x)_(o|x).*\n(o|x){3}.*(\n.*){5}$"), { s, c ->
            (if (s[0] == o) 60.0 else 10.0) * scoreHelperBlankPattern(s.slice(setOf(11, 19, 20)), c)
        }),
        TwoBlanks1(Regex("^__(o|x).*\n(o|x){3}.*(\n.*){6}$"), { s, c ->
            -15.0 * scoreHelperBlankPattern(s.slice(setOf(2, 9, 10, 11)), c)
        }),
        TwoBlanks2(Regex("^((o|x)_(o|x).*\n){2}(o|x){3}.*(\n.*){5}$"), { s, c ->
            -(if (s[0] == o) 30.0 else 5.0) * scoreHelperBlankPattern(s.slice(setOf(2, 11, 19, 20)), c)
        }),
        TwoBlanks3(Regex("^_(o|x).*\n(o|x)_(o|x).*\n(o|x){3}.*(\n.*){5}$"), { s, c ->
            -15.0 * scoreHelperBlankPattern(s.slice(setOf(1, 9, 11, 19, 20)), c)
        }),
        ThreeBlanks1(Regex("^___(o|x).*\n(o|x){4}.*(\n.*){6}$"), { s, c ->
            25.0 * scoreHelperBlankPattern(s.slice(setOf(3, 9, 10, 11, 12)), c)
        }),
        ThreeBlanks2(Regex("^(o|x)_(o|x).*\n__(o|x).*\n(o|x){3}.*(\n.*){5}\$"), { s, c ->
            (if (s[0] == o) 50.0 else 10.0) * scoreHelperBlankPattern(s.slice(setOf(0, 2, 11, 18, 19, 20)), c)
        }),
        ThreeBlanks3(Regex("__(o|x).*\n(o|x)_(o|x).*\n(o|x){3}.*(\n.*){5}\$"), { s, c ->
            25.0 * scoreHelperBlankPattern(s.slice(setOf(2, 9, 11, 18, 19, 20)), c)
        }),
        ;

        fun evaluate(boardString: String, onMove: Boolean): Double = if (regex.matches(boardString)) {
            println("$name, ${computeScore(boardString, onMove)}, $onMove")
            computeScore(boardString, onMove)
        } else 0.0

        companion object {

            fun scoreHelperBlankPattern(surround: String, onMove: Boolean): Int = when {
                x !in surround -> -2
                onMove && o !in surround -> 2
                onMove && o in surround -> 1
                !onMove && o !in surround -> 1
                else -> -2
            }
        }
    }
}

