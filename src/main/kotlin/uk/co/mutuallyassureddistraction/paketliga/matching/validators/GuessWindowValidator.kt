package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import uk.co.mutuallyassureddistraction.paketliga.matching.GuessWindow
import java.time.ZonedDateTime

class GuessWindowValidator {

    fun validateGuessWindow(guessWindow: GuessWindow): String? {
        val startTime = guessWindow.startTime
        val endTime = guessWindow.endTime
        val guessDeadline = guessWindow.guessDeadline
        val now = ZonedDateTime.now()

        if(startTime >= endTime) {
            return "Start time must be before end time"
        }

        if(guessDeadline >= startTime) {
            return "Deadline for guessing should be before the start window"
        }

        if(guessDeadline < now) {
            return "Game can not be in the past"
        }

        return null;
    }
}