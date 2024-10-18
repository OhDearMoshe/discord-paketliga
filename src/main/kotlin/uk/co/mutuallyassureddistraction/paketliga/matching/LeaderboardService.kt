package uk.co.mutuallyassureddistraction.paketliga.matching

import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.dao.PointDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Point

class LeaderboardService(private val pointDao: PointDao) {
    private val logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    fun getLeaderboard(userId: String?): List<Point> {
        try {
            if (userId == null) {
                return pointDao.getPointsSortedByTotalPointsDesc()
            }

            val userPoint = pointDao.getPointByUserId(userId)

            return arrayListOf(userPoint)
        } catch (e: Exception) {
            logger.error("Error while getting leaderboard", e)
        }

        return arrayListOf()
    }
}