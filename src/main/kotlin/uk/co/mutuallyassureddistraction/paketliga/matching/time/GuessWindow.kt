package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.*

data class GuessWindow(val startTime: ZonedDateTime, val endTime: ZonedDateTime, val guessDeadline: ZonedDateTime) {

    fun startAsHumanFriendlyString(): String {
        return toUserFriendlyString(startTime)
    }

    fun endAsHumanFriendlyString(): String {
        return toUserFriendlyString(endTime)
    }

    fun deadlineAsHumanFriendlyString(): String {
        return toUserFriendlyString(guessDeadline)
    }
}

data class UpdateGuessWindow(
    val startTime: ZonedDateTime?,
    val endTime: ZonedDateTime?,
    val guessDeadline: ZonedDateTime?,
) {}
