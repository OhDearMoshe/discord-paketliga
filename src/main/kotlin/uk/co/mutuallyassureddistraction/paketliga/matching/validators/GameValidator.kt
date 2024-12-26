package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.*
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.UpdateGuessWindow

class GameValidator {

    fun validateGameCreate(guessWindow: GuessWindow): String? = validateGuessWindow(guessWindow)

    private fun validateGuessWindow(guessWindow: GuessWindow): String? {
        val startTime = guessWindow.startTime
        val endTime = guessWindow.endTime
        val guessDeadline = guessWindow.guessDeadline
        val now = ZonedDateTime.now()

        return when {
            startTime >= endTime -> DeliveryWindowStartAfterEndError

            guessDeadline >= startTime -> GuessDeadlineAfterWindowStartError

            guessDeadline < now -> GuessDeadlineInPastError

            else -> null
        }
    }

    fun validateGameUpdate(game: Game?, userId: String, updateGuessWindow: UpdateGuessWindow): String? =
        when {
            game == null -> GameValidatorGameIsNullError

            game.userId != userId -> ChangingAnotherUsersGameError

            !game.gameActive -> GameNotActiveError

            updateGuessWindow.startTime == null &&
                updateGuessWindow.endTime == null &&
                updateGuessWindow.guessDeadline == null -> UpdateDidNotChangeAnythingError

            else -> validateGuessWindow(updatedGuessWindowToGuessWindow(updateGuessWindow, game))
        }

    private fun updatedGuessWindowToGuessWindow(updateGuessWindow: UpdateGuessWindow, originalGame: Game) =
        GuessWindow(
            startTime = updateGuessWindow.startTime ?: originalGame.windowStart,
            endTime = updateGuessWindow.endTime ?: originalGame.windowClose,
            guessDeadline = updateGuessWindow.guessDeadline ?: originalGame.guessesClose,
        )
}
