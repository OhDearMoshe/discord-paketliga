package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.dao.StatsDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Carrier
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.UserGame

class StatsService(private val statsDao: StatsDao) {
    fun findCreatedGamesByUsers(): List<UserGame> {
        return statsDao.getGamesCreatedByUsers(1)
    }

    fun findMostPopularCarriers(): List<Carrier> {
        return statsDao.getMostPopularCarriers(1)
    }

    fun findCarriersWithMostVoidedGames(): List<Carrier>{
        return statsDao.getCarriersWithMostVoidedGames(2)
    }

    fun findUsersWithMostVoidedGames(): List<UserGame> {
        return statsDao.getUsersWithMostVoidedGames(1)
    }
}
