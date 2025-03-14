package uk.co.mutuallyassureddistraction.paketliga.dao.entity

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow

const val DEFAULT_CARRIER = "N/A"

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
    val voidedReason: String? = null,
    val carrier: String = DEFAULT_CARRIER,
) {
    fun getGuessWindow() = GuessWindow(startTime = windowStart, endTime = windowClose, guessDeadline = guessesClose)
}
