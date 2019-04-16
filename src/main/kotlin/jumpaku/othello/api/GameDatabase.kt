package jumpaku.othello.api

import jumpaku.commons.control.Result
import jumpaku.commons.control.result
import jumpaku.othello.game.Game
import jumpaku.othello.game.Move
import jumpaku.othello.game.Phase
import jumpaku.othello.selectors.Selector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set


data class UpdateData(val gameId: String, val move: Move)

object GameDatabase {

    private val games: MutableMap<String, Game> = ConcurrentHashMap()

    fun make(): Pair<String, Game> = UUID.randomUUID().toString().let { gameId ->
        games[gameId] = Game()
        gameId to games[gameId]!!
    }

    fun update(gameId: String, move: Move): Result<Pair<String, Game>> = result {
        require(gameId in games) { "specified game is not found" }
        val selector = object : Selector {
            override fun select(phase: Phase): Move = move
        }
        gameId to games.compute(gameId) { _, g -> g?.update(selector) }!!
    }

    operator fun get(gameId: String): Result<Pair<String, Game>> = result {
        require(gameId in games) { "specified game is not found" }
        gameId to games[gameId]!!
    }

    fun delete(gameId: String) {
        games.remove(gameId)
    }
}

