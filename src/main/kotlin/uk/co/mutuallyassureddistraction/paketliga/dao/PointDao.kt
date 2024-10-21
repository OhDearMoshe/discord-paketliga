package uk.co.mutuallyassureddistraction.paketliga.dao

import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Point

interface PointDao {
    @SqlUpdate("""
        INSERT INTO POINT as pnt(
            userId,
            played,
            won,
            lost,
            drawn,
            bonus,
            totalPoint
        )
        VALUES (
            :point.userId,
            :point.played,
            :point.won,
            :point.lost,
            :point.drawn,
            :point.bonus,
            :point.totalPoint
        )
        ON CONFLICT (userId) DO UPDATE
            SET 
                totalPoint = pnt.totalPoint + 1,
                played = pnt.played + 1,
                won = pnt.won + 1
            WHERE pnt.userId = :point.userId
        RETURNING *
    """)
    fun addWin(point: Point)

    @SqlUpdate("""
        INSERT INTO POINT as pnt(
            userId,
            played,
            won,
            lost,
            drawn,
            bonus,
            totalPoint
        )
        VALUES (
            :point.userId,
            :point.played,
            :point.won,
            :point.lost,
            :point.drawn,
            :point.bonus,
            :point.totalPoint
        )
        ON CONFLICT (userId) DO UPDATE
            SET 
                totalPoint = pnt.totalPoint + 2,
                played = pnt.played + 1,
                won = pnt.won + 1,
                bonus = pnt.bonus + 1
            WHERE pnt.userId = :point.userId
        RETURNING *
    """)
    fun addBonusWin(point: Point)

    @SqlUpdate("""
        INSERT INTO POINT as pnt(
            userId,
            played,
            won,
            lost,
            drawn,
            bonus,
            totalPoint
        )
        VALUES (
            :point.userId,
            :point.played,
            :point.won,
            :point.lost,
            :point.drawn,
            :point.bonus,
            :point.totalPoint
        )
        ON CONFLICT (userId) DO UPDATE
            SET 
                totalPoint = pnt.totalPoint + 0.5,
                played = pnt.played + 1,
                drawn = pnt.drawn + 1
            WHERE pnt.userId = :point.userId
        RETURNING *
    """)
    fun addDraw(point: Point)

    @SqlUpdate("""
        INSERT INTO POINT as pnt(
            userId,
            played,
            won,
            lost,
            drawn,
            bonus,
            totalPoint
        )
        VALUES (
            :point.userId,
            :point.played,
            :point.won,
            :point.lost,
            :point.drawn,
            :point.bonus,
            :point.totalPoint
        )
        ON CONFLICT (userId) DO UPDATE
            SET 
                played = pnt.played + 1,
                lost = pnt.lost + 1
            WHERE pnt.userId = :point.userId
        RETURNING *
    """)
    fun addLost(point: Point)

    @SqlQuery("""
        SELECT * FROM POINT
        ORDER BY totalPoint DESC
        LIMIT :limit;
    """)
    fun getPointsSortedByTotalPointsDesc(limit: Int?): List<Point>

    @SqlQuery("""
        SELECT * FROM POINT
        WHERE userId = :userId
    """)
    fun getPointByUserId(userId: String): Point
}