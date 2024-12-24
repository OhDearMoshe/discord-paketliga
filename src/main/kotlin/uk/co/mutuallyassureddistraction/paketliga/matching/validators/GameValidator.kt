package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
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
            startTime >= endTime ->
                "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window"

            guessDeadline >= startTime ->
                "<:ohno:760904962108162069> Deadline for guesses must be before the delivery window opens"

            guessDeadline < now -> "<:pikachu:918170411605327924> Deadline for guesses can't be in the past."

            else -> null
        }
    }

    fun validateGameUpdate(game: Game?, userId: String, updateGuessWindow: UpdateGuessWindow): String? =
        when {
            game == null -> "Inactive or invalid game ID. Double-check and try again"

            game.userId != userId -> "Mr Pump stops you from interfering with another persons mail"

            !game.gameActive -> "Game (already) over, man. Should have sent this update first class"

            updateGuessWindow.startTime == null &&
                updateGuessWindow.endTime == null &&
                updateGuessWindow.guessDeadline == null -> "<:thonk:344120216227414018> You didn't change anything"

            else -> validateGuessWindow(updatedGuessWindowToGuessWindow(updateGuessWindow, game))
        }

    private fun updatedGuessWindowToGuessWindow(updateGuessWindow: UpdateGuessWindow, originalGame: Game) =
        GuessWindow(
            startTime = updateGuessWindow.startTime ?: originalGame.windowStart,
            endTime = updateGuessWindow.endTime ?: originalGame.windowClose,
            guessDeadline = updateGuessWindow.guessDeadline ?: originalGame.guessesClose,
        )
}
