package uk.co.mutuallyassureddistraction.paketliga.matching

import dev.kord.core.entity.Member
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
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
                    )
                )
            val gameNameString = gameNameStringMaker(gameName, createdGame.gameId!!)
            return ":postal_horn: $gameNameString | ${member?.mention ?: username}'s package is arriving between ${guessWindow.startAsHumanFriendlyString()} and " +
                "${guessWindow.endAsHumanFriendlyString()}. " +
                "Guesses accepted until ${guessWindow.deadlineAsHumanFriendlyString()}"
        } catch (e: Exception) {
            logger.error("Error while creating game", e)
            return "<:pressf:692833208382914571> You done goofed. Check your inputs and try again."
        }
    }

    fun updateGame(
        gameId: Int,
        userId: String,
        member: Member?,
        updateGuessWindow: UpdateGuessWindow,
        username: String
    ): Pair<Array<String>, List<String>> {
        try {
            val originalGame = gameDao.findActiveGameById(gameId)
            val updateErrorMessage = gameValidator.validateGameUpdate(originalGame, userId, updateGuessWindow)
            if (updateErrorMessage != null) {
                return Pair(arrayOf(updateErrorMessage), arrayListOf(""))
            }

            val updatedGame =
                gameDao.updateGameTimes(
                    gameId,
                    updateGuessWindow.startTime,
                    updateGuessWindow.endTime,
                    updateGuessWindow.guessDeadline,
                )
            val guessWindow = updatedGame.getGuessWindow()
            val gameUpdatedString: String =
                ":postal_horn: #$gameId has been updated|  ${member?.mention ?: username}'s package is now arriving between " +
                    "${guessWindow.startAsHumanFriendlyString()} and ${guessWindow.endAsHumanFriendlyString()}. " +
                    "Guesses accepted until ${guessWindow.deadlineAsHumanFriendlyString()}"

            val guesses = guessFinderService.findGuesses(gameId, null)
            val userIds = guesses.map { it.userId }.toList()

            return Pair(arrayOf(gameUpdatedString), userIds)
        } catch (e: Exception) {
            logger.error("Error while updating game", e)
            return Pair(
                arrayOf("<:pressf:692833208382914571> You done goofed. Check your inputs and try again. "),
                arrayListOf(),
            )
        }
    }

    // We need username for non-server users that are using this command, if any (hence the nullable Member)
    // Kinda unlikely, but putting this here just in case
    private fun gameNameStringMaker(gameName: String?, gameId: Int): String =
        "$gameName (#$gameId)"
}
