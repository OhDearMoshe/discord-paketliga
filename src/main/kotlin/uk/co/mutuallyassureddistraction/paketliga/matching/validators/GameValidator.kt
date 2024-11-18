package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.UpdateGuessWindow
import java.time.ZonedDateTime

class GameValidator {

    fun validateGameCreate(guessWindow: GuessWindow): String? {
        return validateGuessWindow(guessWindow)
    }
    fun validateGuessWindow(guessWindow: GuessWindow): String? {
        val startTime = guessWindow.startTime
        val endTime = guessWindow.endTime
        val guessDeadline = guessWindow.guessDeadline
        val now = ZonedDateTime.now()

        if(startTime >= endTime) {
            return ":notstonks: Start of the delivery window must be before the end of the delivery window"
        }

        if(guessDeadline >= startTime) {
            return ":ohno: Deadline for guesses must be before the delivery window opens"
        }

        if(guessDeadline < now) {
            return ":pikachu: Deadline for guesses can't be in the past."
        }
        return null;
    }

    fun validateGameUpdate(game: Game?, userId: String, updateGuessWindow: UpdateGuessWindow): String? {
        if(game == null) {
            return "Wrong Game ID, please check your gameId input and try again"
        }
        if (game.userId != userId) {
            return "Mr Pump stops you from interfering with another persons mail"
        }
        if(!game.gameActive) {
            return "Game is already over. Should have sent this update first class"
        }

        if(updateGuessWindow.startTime == null && updateGuessWindow.endTime == null && updateGuessWindow.guessDeadline == null) {
            return "You appear to have forgotten to actually update anything"
        }
        return validateGuessWindow(updatedGuessWindowToGuessWindow(updateGuessWindow, game))
    }

    fun updatedGuessWindowToGuessWindow(updateGuessWindow: UpdateGuessWindow, originalGame: Game): GuessWindow {
        return GuessWindow(
            startTime = updateGuessWindow.startTime ?: originalGame.windowStart,
            endTime = updateGuessWindow.endTime ?: originalGame.windowClose,
            guessDeadline = updateGuessWindow.guessDeadline ?: originalGame.guessesClose
        )
    }

}