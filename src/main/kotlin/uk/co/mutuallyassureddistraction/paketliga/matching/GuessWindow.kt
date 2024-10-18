package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.*
import java.time.format.DateTimeFormatter

private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm")

data class GuessWindow(val startTime: ZonedDateTime,
                       val endTime: ZonedDateTime,
                        val guessDeadline: ZonedDateTime){

    fun startAsHumanFriendlyString(): String {
        return startTime.format(dtf)
    }

    fun endAsHumanFriendlyString(): String {
        return endTime.format(dtf)
    }

    fun deadlineAsHumanFriendlyString(): String {
        return guessDeadline.format(dtf)
    }
}

data class UpdateGuessWindow(val startTime: ZonedDateTime?,
                             val endTime: ZonedDateTime?,
                             val guessDeadline: ZonedDateTime?) {

}