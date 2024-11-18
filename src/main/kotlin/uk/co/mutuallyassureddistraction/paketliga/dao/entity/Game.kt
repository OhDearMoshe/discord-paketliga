package uk.co.mutuallyassureddistraction.paketliga.dao.entity

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow

data class Game(
    val gameId: Int? = null,
    val gameName: String,
    val windowStart: ZonedDateTime,
    val windowClose: ZonedDateTime,
    val guessesClose: ZonedDateTime,
    val deliveryTime: ZonedDateTime? = null,
    val userId: String,
    val gameActive: Boolean,
    val gameVoided: Boolean = false,
) {
    fun getGuessWindow(): GuessWindow {
        return GuessWindow(startTime = windowStart, endTime = windowClose, guessDeadline = guessesClose)
    }
}
