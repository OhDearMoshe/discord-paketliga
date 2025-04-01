package uk.co.mutuallyassureddistraction.paketliga.dao

import org.jdbi.v3.sqlobject.statement.SqlQuery
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.GameCreated

interface StatsDao {
    @SqlQuery(
        """
        SELECT a.userId, COUNT(userId) as gameCount,
            (
                SELECT carrier mostCarrier
                FROM Game g
                WHERE g.userId = a.userId
                GROUP BY carrier
                ORDER BY COUNT(carrier) DESC
                LIMIT 1
            )
        FROM Game a
        GROUP BY a.userId
        ORDER BY gameCount
        DESC
    """
    )
    fun getGameCreatedSortedByCountDesc(): List<GameCreated>
}
