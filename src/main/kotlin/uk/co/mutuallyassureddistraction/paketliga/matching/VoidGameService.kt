package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.ZonedDateTime
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.VoidGameServiceChangingAnotherUsersGameError
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.gameVoidedMessage
import uk.co.mutuallyassureddistraction.paketliga.voidGameServiceGameIsNullError

class VoidGameService(private val gameDao: GameDao) {

    private val logger = LoggerFactory.getLogger(VoidGameService::class.java)

    fun voidGame(gameId: Int, reason: String?, userId: String): String {
        val game = gameDao.findActiveGameById(gameId) ?: return voidGameServiceGameIsNullError(gameId)

        if (game.userId != userId) {
            return VoidGameServiceChangingAnotherUsersGameError
        }
        gameDao.voidGameById(gameId, reason)
        return gameVoidedMessage(gameId)
    }

    fun cullExpiredGames() {
        logger.info("Culling games that ended more than 24 hours ago")
        val activeGames = gameDao.findActiveGames(null, null)
        val expiryTime = ZonedDateTime.now().minusHours(24)
        activeGames
            .filter { it.windowClose < expiryTime }
            .forEach {
                logger.info("Voiding game ${it.gameId} that ended ${it.windowClose}")
                gameDao.voidGameById(it.gameId!!, "Game became stale and expired")
            }
    }
}
