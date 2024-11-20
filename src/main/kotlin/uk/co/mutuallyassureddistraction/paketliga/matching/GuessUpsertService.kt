package uk.co.mutuallyassureddistraction.paketliga.matching

import java.sql.SQLException
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GuessValidator

class GuessUpsertService(
    private val guessDao: GuessDao,
    private val gameDao: GameDao,
    private val guessValidator: GuessValidator,
) {

    private val logger = LoggerFactory.getLogger(GuessUpsertService::class.java)

    fun guessGame(gameId: Int, guessTime: GuessTime, userId: String, userMention: String): String {

        try {
            val searchedGame = gameDao.findActiveGameById(gameId)
            val errorMessage = guessValidator.validateGuess(searchedGame, gameId, userId, guessTime)
            if (errorMessage != null) {
                return errorMessage
            }
            guessDao.createGuess(
                Guess(guessId = null, gameId = gameId, guessTime = guessTime.guessTime, userId = userId)
            )
            return "<:sickos:918170456190775348> $userMention has guessed ${guessTime.toHumanString()} for game ID #$gameId"
        } catch (e: Exception) {
            var errorString = "<:pressf:692833208382914571> You done goofed. Check your inputs and try again."
            when (e) {
                is UnableToExecuteStatementException -> {
                    // TODO: Create sql code error resolver. Also move some of the validations out into triggers
                    val original = e.cause as SQLException
                    when (original.sqlState) {
                        "23505" -> {
                            errorString = "*\\*womp-womp*\\* Your guess isn't within the delivery window"
                        }
                        "ERRA1" -> {
                            errorString = "*\\*womp-womp*\\* Game ID #$gameId is not valid or is no longer active "
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
