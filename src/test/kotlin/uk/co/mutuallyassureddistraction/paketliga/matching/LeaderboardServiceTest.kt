package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.PointDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Point

class LeaderboardServiceTest {
    private lateinit var target: LeaderboardService
    private lateinit var pointDao: PointDao

    @DisplayName("getLeaderboard() with userid will return one point")
    @Test
    fun whenGetWithUserIdReturnOnePoint() {
        pointDao = mockk<PointDao>()
        every { pointDao.getPointByUserId(any()) } returns Point(2, "Y", 1, 1, 1, 0, 0, 3F)
        target = LeaderboardService(pointDao)

        val points = target.getLeaderboard("Y", null)
        assertEquals(points.size, 1)
        assertEquals(points[0].userId, "Y")
    }

    @DisplayName("getLeaderboard() with null userid will return all points")
    @Test
    fun whenGetWithoutUserIdReturnAllPoints() {
        pointDao = mockk<PointDao>()
        every { pointDao.getPointsSortedByTotalPointsDesc(null) } returns
            arrayListOf(Point(2, "Y", 1, 1, 1, 0, 0, 3F), Point(1, "Z", 1, 1, 1, 0, 0, 1F))
        target = LeaderboardService(pointDao)

        val points = target.getLeaderboard(null, null)
        assertEquals(points.size, 2)
        assertEquals(points[0].userId, "Y")
        assertEquals(points[1].userId, "Z")
    }

    @DisplayName("getLeaderboard() with same points will be sorted based on wins/played ratio ")
    @Test
    fun whenGetWithSamePointsThenSortByWinsAndPlayedRatio() {
        pointDao = mockk<PointDao>()
        every { pointDao.getPointsSortedByTotalPointsDesc(null) } returns
            arrayListOf(
                Point(4, "W", 5, 3, 0, 0, 0, 3F),
                Point(3, "X", 8, 3, 0, 0, 0, 3F),
                Point(2, "Y", 3, 3, 0, 0, 0, 3F),
                Point(1, "Z", 1, 1, 1, 0, 0, 1F),
                Point(1, "V", 7, 3, 4, 0, 0, 3F),
            )
        target = LeaderboardService(pointDao)

        val points = target.getLeaderboard(null, null)
        assertEquals(points.size, 5)
        assertEquals(points[0].userId, "Y")
        assertEquals(points[1].userId, "W")
        assertEquals(points[2].userId, "V")
        assertEquals(points[3].userId, "X")
        assertEquals(points[4].userId, "Z")
    }
}
