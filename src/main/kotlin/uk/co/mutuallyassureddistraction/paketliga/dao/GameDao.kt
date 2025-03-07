package uk.co.mutuallyassureddistraction.paketliga.dao

import java.time.ZonedDateTime
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game

interface GameDao {
    @SqlQuery(
        """
          INSERT INTO GAME(
               gameName,
               windowStart,
               windowClose,
               guessesClose,
               deliveryTime,
               userId,
               gameActive,
               gameVoided,
               carrier
          )
          VALUES (
               :game.gameName,
               :game.windowStart,
               :game.windowClose,
               :game.guessesClose,
               :game.deliveryTime,
               :game.userId,
               :game.gameActive,
               :game.gameVoided,
               :game.carrier
          )
          RETURNING *
     """
    )
    fun createGame(game: Game): Game

    @SqlQuery(
        """
          UPDATE GAME
          SET 
               windowStart = COALESCE(:windowStart, windowStart),
               windowClose = COALESCE(:windowClose, windowClose),
               guessesClose = COALESCE(:guessesClose, windowClose),
               carrier = COALESCE(:carrier, carrier)
          WHERE gameId = :id
          RETURNING *
     """
    )
    fun updateGameTimes(
        @Bind("id") gameId: Int,
        @Bind("windowStart") windowStart: ZonedDateTime?,
        @Bind("windowClose") windowClose: ZonedDateTime?,
        @Bind("guessesClose") guessesClose: ZonedDateTime?,
        @Bind("carrier") carrier: String?,
    ): Game

    @SqlQuery(
        """
          UPDATE Game
          SET
               deliveryTime = :deliveryTime,
               gameActive = 'FALSE'
          WHERE gameId = :id
          RETURNING *
     """
    )
    fun finishGame(@Bind("id") gameId: Int, @Bind("deliveryTime") deliveryTime: ZonedDateTime): Game

    @SqlQuery(
        """
          SELECT * FROM GAME
          WHERE gameId = :id
          AND gameActive = 'TRUE'
     """
    )
    fun findActiveGameById(@Bind("id") gameId: Int): Game?

    @SqlQuery(
        """
          SELECT * FROM GAME
          WHERE (:gameName IS NULL OR gameName LIKE concat('%',:gameName,'%'))
          AND (:userId is NULL OR userId = :userId)
          AND gameActive = 'TRUE'
     """
    )
    fun findActiveGames(gameName: String?, userId: String?): List<Game>

    @SqlQuery(
        """
          UPDATE Game
          SET
               gameActive = 'FALSE',
               gameVoided = 'TRUE',
               voidedReason = :voidedReason
          WHERE gameId = :id
          RETURNING *
     """
    )
    fun voidGameById(@Bind("id") gameId: Int, @Bind("voidedReason") voidedReason: String?): Game
}
