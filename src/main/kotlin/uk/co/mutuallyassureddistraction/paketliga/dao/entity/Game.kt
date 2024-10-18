package uk.co.mutuallyassureddistraction.paketliga.dao.entity

import uk.co.mutuallyassureddistraction.paketliga.matching.GuessWindow
import java.time.ZonedDateTime

data class Game(
    val gameId: Int?,
    val gameName: String,
    val windowStart: ZonedDateTime,
    val windowClose: ZonedDateTime,
    val guessesClose: ZonedDateTime,
    val deliveryTime: ZonedDateTime?,
    val userId: String,
    val gameActive: Boolean
) {
    fun getGuessWindow(): GuessWindow {
        return GuessWindow(
            startTime = windowStart,
            endTime = windowClose,
            guessDeadline = guessesClose
        )
    }
}
