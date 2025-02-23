package uk.co.mutuallyassureddistraction.paketliga.matching

import dev.kord.core.entity.Member
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.GameCreationErrorMessage
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.DEFAULT_CARRIER
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.gameAnnouncementMessage
import uk.co.mutuallyassureddistraction.paketliga.gameUpdateMessage
import uk.co.mutuallyassureddistraction.paketliga.gameUpdatedOnlyCarrier
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.UpdateGuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GameValidator

class GameUpsertService(
    private val gameDao: GameDao,
    private val guessFinderService: GuessFinderService,
    private val gameValidator: GameValidator,
) {

    private val logger = LoggerFactory.getLogger(GameUpsertService::class.java)

    fun createGame(
        userGameName: String?,
        guessWindow: GuessWindow,
        carrier: String?,
        userId: String,
        member: Member?,
        username: String,
    ): String {
        try {
            val validationErrorMessage = gameValidator.validateGameCreate(guessWindow)

            if (validationErrorMessage != null) {
                return validationErrorMessage
            }
            // Start or end doesn't matter if we only have one date at a time
            val gameName = userGameName ?: "Game"

            val createdGame =
                gameDao.createGame(
                    Game(
                        gameId = null,
                        gameName = gameName,
                        windowStart = guessWindow.startTime,
                        windowClose = guessWindow.endTime,
                        guessesClose = guessWindow.guessDeadline,
                        deliveryTime = null,
                        userId = userId,
                        gameActive = true,
                        carrier = carrier ?: DEFAULT_CARRIER,
                    )
                )
            return gameAnnouncementMessage(gameName, member, username, createdGame.gameId!!, guessWindow, carrier)
        } catch (e: Exception) {
            logger.error("Error while creating game", e)
            return GameCreationErrorMessage
        }
    }

    fun updateGame(
        gameId: Int,
        userId: String,
        member: Member?,
        updateGuessWindow: UpdateGuessWindow,
        carrier: String?,
        username: String,
    ): Pair<Array<String>, List<String>> {
        try {
            val originalGame = gameDao.findActiveGameById(gameId)
            val updateErrorMessage = gameValidator.validateGameUpdate(originalGame, userId, updateGuessWindow, carrier)
            if (updateErrorMessage != null) {
                return Pair(arrayOf(updateErrorMessage), arrayListOf(""))
            }

            val updatedGame =
                gameDao.updateGameTimes(
                    gameId,
                    updateGuessWindow.startTime,
                    updateGuessWindow.endTime,
                    updateGuessWindow.guessDeadline,
                    carrier,
                )
            val guessWindow = updatedGame.getGuessWindow()
            val gameUpdatedString =
                if (updateGuessWindow.isEmpty()) {
                    gameUpdatedOnlyCarrier(gameId, carrier!!, member, username)
                } else {
                    gameUpdateMessage(gameId, guessWindow, carrier, member, username)
                }

            val guesses = guessFinderService.findGuesses(gameId, null)
            val userIds = guesses.map { it.userId }.toList()

            return Pair(arrayOf(gameUpdatedString), userIds)
        } catch (e: Exception) {
            logger.error("Error while updating game", e)
            return Pair(arrayOf(GameCreationErrorMessage), arrayListOf())
        }
    }
}
