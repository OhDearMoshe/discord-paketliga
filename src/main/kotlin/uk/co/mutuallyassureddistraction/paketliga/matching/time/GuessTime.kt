package uk.co.mutuallyassureddistraction.paketliga.matching.time


import java.time.ZonedDateTime

data class GuessTime(val guessTime: ZonedDateTime) {

    fun toHumanString(): String {
        return toUserFriendlyString(guessTime)
    }
}
