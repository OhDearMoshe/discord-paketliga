package uk.co.mutuallyassureddistraction.paketliga.matching

import java.math.RoundingMode
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.dao.PointDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Point

class LeaderboardService(private val pointDao: PointDao) {
    private val logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    fun getLeaderboard(userId: String?, limit: Int?): List<Point> {
        try {
            if (userId == null) {
                return sortByWinsAndPlayedRatio(pointDao.getPointsSortedByTotalPointsDesc(limit))
            }

            val userPoint = pointDao.getPointByUserId(userId)

            return arrayListOf(userPoint)
        } catch (e: Exception) {
            logger.error("Error while getting leaderboard", e)
        }

        return arrayListOf()
    }

    private fun sortByWinsAndPlayedRatio(points: List<Point>): List<Point> {
        val comparator = Comparator { p1: Point, p2: Point ->
            if (p1.totalPoint.equals(p2.totalPoint)) {
                val p1Ratio = p1.won.toBigDecimal().divide(p1.played.toBigDecimal(), 4, RoundingMode.FLOOR)
                val p2Ratio = p2.won.toBigDecimal().divide(p2.played.toBigDecimal(), 4, RoundingMode.FLOOR)

                return@Comparator p2Ratio.compareTo(p1Ratio)
            }

            return@Comparator p2.totalPoint.compareTo(p1.totalPoint)
        }

        val copy = arrayListOf<Point>().apply { addAll(points) }
        copy.sortWith(comparator)
        return copy
    }
}
