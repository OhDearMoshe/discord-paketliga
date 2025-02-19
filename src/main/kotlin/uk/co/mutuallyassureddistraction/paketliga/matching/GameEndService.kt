package uk.co.mutuallyassureddistraction.paketliga.matching

import java.sql.SQLException
import java.time.ZonedDateTime
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.*
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.results.GameResult
import uk.co.mutuallyassureddistraction.paketliga.matching.results.GameResultResolver
import uk.co.mutuallyassureddistraction.paketliga.matching.results.PointUpdaterService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.DeliveryTime

class GameEndService(
    private val guessDao: GuessDao,
    private val gameDao: GameDao,
    private val gameResultResolver: GameResultResolver,
    private val pointUpdaterService: PointUpdaterService,
) {

    private val logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    fun endGame(gameId: Int, deliveryTime: DeliveryTime): Pair<String?, GameResult?> {
        var searchedGame: Game = gameDao.findActiveGameById(gameId) ?: return Pair(GameEndServiceGameIsNullError, null)

        // 1. we finish the game
        try {
            if (isGameVoid(searchedGame, deliveryTime)) {
                // Void the game
                gameDao.voidGameById(searchedGame.gameId!!, DeliveryTimeOutsideWindowVoidReason)
                return Pair(DeliveryTimeOutsideWindowGameVoidMessage, null)
            }
            searchedGame = gameDao.finishGame(gameId, deliveryTime.deliveryTime)
        } catch (e: Exception) {
            return handleExceptions(e, deliveryTime.deliveryTime, gameId)
        }

        // 2. we get the guesses and find the winning guess(es)
        val guesses = guessDao.findGuessesByGameId(gameId)
        val result = gameResultResolver.findWinners(searchedGame, guesses)

        // 3. We apply some tasty tasty points
        pointUpdaterService.applyPoints(result)
        return Pair(null, result)
    }

    private fun isGameVoid(game: Game, deliveryTime: DeliveryTime): Boolean =
        deliveryTime.deliveryTime < game.guessesClose ||
            deliveryTime.deliveryTime.dayOfYear > game.windowClose.dayOfYear

    private fun handleExceptions(
        e: Exception,
        zonedDeliveryDateTime: ZonedDateTime,
        gameId: Int,
    ): Pair<String?, GameResult?> {
        var errorString = GameEndServiceGenericErrorMessage
        when (e) {
            is UnableToExecuteStatementException -> {
                val original = e.cause as SQLException
                when (original.sqlState) {
                    "ERRG0" -> {
                        errorString =
                            failedToEndGameDueToDeliveryTimeOutOfWindowErrorMessage(zonedDeliveryDateTime, gameId)
                    }
                }
            }

            else -> {
                logger.error("Error while ending a game", e)
            }
        }
        return Pair(errorString, null)
    }
}
