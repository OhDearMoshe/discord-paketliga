package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class FindGamesResponse(
    val gameId: Int,
    val gameName: String,
    val userId: String,
    val windowStart: ZonedDateTime,
    val windowClose: ZonedDateTime,
    val guessesClose: ZonedDateTime,
) {
    fun startAsHumanFriendlyString(): String = windowStart.toUserFriendlyString()

    fun endAsHumanFriendlyString(): String = windowClose.toUserFriendlyString()

    fun guessesCloseAsHumanFriendlyString(): String = guessesClose.toUserFriendlyString()
}
