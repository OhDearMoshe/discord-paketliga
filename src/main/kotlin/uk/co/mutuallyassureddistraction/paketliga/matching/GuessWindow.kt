package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.*
import java.time.format.DateTimeFormatter

private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm")

data class GuessWindow(val startTime: String,
                       val endTime: String,
                        val guessDeadline: String){

    fun startAsDate(): ZonedDateTime {
        return ZonedDateTime.of(LocalDate.parse(startTime, dtf), LocalTime.now(), ZoneId.systemDefault())
    }

    fun endAsDate(): ZonedDateTime {
        return ZonedDateTime.of(LocalDate.parse(endTime, dtf), LocalTime.now(), ZoneId.systemDefault())
    }

    fun deadlineAsDate(): ZonedDateTime {
        return ZonedDateTime.of(LocalDate.parse(guessDeadline, dtf), LocalTime.now(), ZoneId.systemDefault())
    }
}
