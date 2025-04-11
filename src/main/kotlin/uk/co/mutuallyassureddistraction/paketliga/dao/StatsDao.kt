package uk.co.mutuallyassureddistraction.paketliga.dao

import org.jdbi.v3.sqlobject.statement.SqlQuery
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Carrier
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.UserGame

interface StatsDao {
    @SqlQuery(
        """
        SELECT userId, COUNT(userId) as gameCount
        FROM Game
        GROUP BY userId
        ORDER BY gameCount
        DESC
        LIMIT :limit;
    """
    )
    fun getGamesCreatedByUsers(limit: Int?): List<UserGame>

    @SqlQuery(
        """
        SELECT carrier, count(carrier) as carrierCount
        FROM Game
        WHERE carrier != 'N/A'
        GROUP BY carrier
        ORDER BY carrierCount
        DESC
        LIMIT :limit;
    """
    )
    fun getMostPopularCarriers(limit: Int?): List<Carrier>

    @SqlQuery(
        """
        SELECT carrier, count(carrier) as carrierCount
        FROM Game
        WHERE gameVoided = 'TRUE'
        AND carrier != 'N/A'
        GROUP BY carrier
        ORDER BY carrierCount
        DESC
        LIMIT :limit;
    """
    )
    fun getCarriersWithMostVoidedGames(limit: Int?): List<Carrier>

    @SqlQuery(
        """
        SELECT userId, COUNT(userId) as gameCount
        FROM Game
        WHERE gameVoided = 'TRUE'
        GROUP BY userId
        ORDER BY gameCount
        DESC
        LIMIT :limit;
    """
    )
    fun getUsersWithMostVoidedGames(limit: Int?): List<UserGame>
}
