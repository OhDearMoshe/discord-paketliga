package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.ZonedDateTime

class FindGamesResponse(
    val gameId: Int,
    val gameName: String,
    val userId: String,
    val windowStart: ZonedDateTime,
    val windowClose: ZonedDateTime,
    val guessesClose: ZonedDateTime,
)
