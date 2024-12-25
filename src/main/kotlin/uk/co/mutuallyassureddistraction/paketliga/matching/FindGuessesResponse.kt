package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.ZonedDateTime

data class FindGuessesResponse(val guessId: Int, val gameId: Int, val userId: String, val guessTime: ZonedDateTime)
