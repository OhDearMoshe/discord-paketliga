package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime

data class UpdateGuessWindow(
    val startTime: ZonedDateTime?,
    val endTime: ZonedDateTime?,
    val guessDeadline: ZonedDateTime?,
)
