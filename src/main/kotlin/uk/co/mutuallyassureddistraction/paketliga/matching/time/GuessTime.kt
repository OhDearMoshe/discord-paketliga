package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime

data class GuessTime(val guessTime: ZonedDateTime) {
    fun toHumanString(): String = guessTime.toUserFriendlyString()

    fun isMakingAWish(): Boolean {
        return guessTime.hour == 11 && guessTime.minute == 11
    }
}
