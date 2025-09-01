package uk.co.mutuallyassureddistraction.paketliga.services

import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import java.time.Duration
import java.time.ZonedDateTime
import java.util.logging.Logger

class StaleGameCullService(private val gameDao: GameDao) {

    private val LOGGER = Logger.getLogger(StaleGameCullService::class.java.name)

    fun cullStaleGames() {
        val activeGames = gameDao.findActiveGames(null, null)
        warnOnAlmostStaleGames(activeGames)
        cullStaleGame(activeGames)

    }

    private fun warnOnAlmostStaleGames(games: List<Game>) {
        val now = ZonedDateTime.now()
        games.filter { game ->
             val age = Duration.between(now, game.windowClose).toHours()
            age in 24..48
        }
            .forEach { LOGGER.info("Game ${it.gameId!!} is in cull window") }
    }

    private fun cullStaleGame(games: List<Game>) {
        val now = ZonedDateTime.now()
        games.filter { game ->
            val age = Duration.between(now, game.windowClose).toHours()
            age > 48
        }
            .forEach { gameDao.voidGameById(it.gameId!!, "Culled by Dr Packidge") }
    }

}