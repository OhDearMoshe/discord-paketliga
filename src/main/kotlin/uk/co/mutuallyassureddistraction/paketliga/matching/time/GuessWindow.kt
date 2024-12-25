package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime

data class GuessWindow(val startTime: ZonedDateTime, val endTime: ZonedDateTime, val guessDeadline: ZonedDateTime) {
    fun startAsHumanFriendlyString(): String = startTime.toUserFriendlyString()

    fun endAsHumanFriendlyString(): String = endTime.toUserFriendlyString()

    fun deadlineAsHumanFriendlyString(): String = guessDeadline.toUserFriendlyString()
}
