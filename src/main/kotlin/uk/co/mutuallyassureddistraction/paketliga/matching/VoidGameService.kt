package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao

class VoidGameService(private val gameDao: GameDao) {

    fun voidGame(gameId: Int, reason: String?, userId: String): String {
        val game = gameDao.findActiveGameById(gameId) ?: return "Game $gameId was not found"

        if (game.userId != userId) {
            return "Mr Pump prevents you from interfering with another game"
        }
        gameDao.voidGameById(gameId, reason)
        return "Game $gameId has been voided"
    }
}
