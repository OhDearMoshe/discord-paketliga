package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao

class VoidGameService(private val gameDao: GameDao) {

    fun voidGame(gameId: Int, userId: String): String {
        val game = gameDao.findActiveGameById(gameId)

        if(game == null) {
            return "Game $gameId was not found"
        }

        if(game.userId != userId) {
            return "Mr Pump says you cannot interfere with another game"
        }
        gameDao.voidGameById(gameId)
        return "Game $gameId has been voided"
    }
}