package uk.co.mutuallyassureddistraction.paketliga.matching

import java.sql.SQLException
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.GuessCreationErrorMessage
import uk.co.mutuallyassureddistraction.paketliga.GuessNotWithinDeliveryWindowErrorMessage
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import uk.co.mutuallyassureddistraction.paketliga.gameNotValidOrActiveErrorMessage
import uk.co.mutuallyassureddistraction.paketliga.guessCreationMessage
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessNudger
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GuessValidator

class GuessUpsertService(
    private val guessDao: GuessDao,
    private val gameDao: GameDao,
    private val guessValidator: GuessValidator,
    private val guessNudger: GuessNudger,
) {

    private val logger = LoggerFactory.getLogger(GuessUpsertService::class.java)

    fun guessGame(gameId: Int, guessTime: GuessTime, userId: String, userMention: String): String {

        try {
            val searchedGame = gameDao.findActiveGameById(gameId)
            val finalisedGuessTime = guessNudger.nudgeGuessToDeliveryDay(searchedGame, guessTime)
            val errorMessage = guessValidator.validateGuess(searchedGame, gameId, userId, finalisedGuessTime)
            if (errorMessage != null) {
                return errorMessage
            }
            guessDao.createGuess(
                Guess(guessId = null, gameId = gameId, guessTime = finalisedGuessTime.guessTime, userId = userId)
            )
            return guessCreationMessage(userMention, finalisedGuessTime, gameId)
        } catch (e: Exception) {
            var errorString = GuessCreationErrorMessage
            when (e) {
                is UnableToExecuteStatementException -> {
                    // TODO: Create sql code error resolver. Also move some of the validations out into triggers
                    val original = e.cause as SQLException
                    when (original.sqlState) {
                        "23505" -> {
                            errorString = GuessNotWithinDeliveryWindowErrorMessage
                        }
                        "ERRA1" -> {
                            errorString = gameNotValidOrActiveErrorMessage(gameId)
                        }
                    }
                }
                else -> {
                    logger.error("Error while guessing ${e.message} ${e.stackTrace}")
                }
            }
            return errorString
        }
    }
}
