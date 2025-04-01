package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.dao.StatsDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.GameCreated

class StatsService(private val statsDao: StatsDao) {
    fun findCreatedGames(): List<GameCreated> {
        return statsDao.getGameCreatedSortedByCountDesc()
    }
}
