package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.VoidGameServiceChangingAnotherUsersGameError
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.gameVoidedMessage
import uk.co.mutuallyassureddistraction.paketliga.voidGameServiceGameIsNullError

class VoidGameService(private val gameDao: GameDao) {

    fun voidGame(gameId: Int, reason: String?, userId: String): String {
        val game = gameDao.findActiveGameById(gameId) ?: return voidGameServiceGameIsNullError(gameId)

        if (game.userId != userId) {
            return VoidGameServiceChangingAnotherUsersGameError
        }
        gameDao.voidGameById(gameId, reason)
        return gameVoidedMessage(gameId)
    }
}
