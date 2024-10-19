package uk.co.mutuallyassureddistraction.paketliga.matching.time

import org.joda.time.DateTime
import java.time.ZonedDateTime

class GameTimeParserService(private val timeParser: TimeParser) {

    fun parseGameTime(startWindow: String, closeWindow: String, guessesClose: String?): GuessWindow {
        val startDate = timeParser.parseDate(startWindow)
        return GuessWindow(
            startTime = startDate,
            endTime = timeParser.parseDate(closeWindow),
            guessDeadline = resolveCloseTime(startDate, guessesClose)
        )
    }

    fun parseGameUpdateTime(startWindow: String?, closeWindow: String?, guessesClose: String?): UpdateGuessWindow {
        return UpdateGuessWindow(
            startTime = startWindow?.let { timeParser.parseDate(it) },
            endTime = closeWindow?.let { timeParser.parseDate(it) },
            guessDeadline = guessesClose?.let { timeParser.parseDate(it) }
        )
    }

    private fun resolveCloseTime(startDate: ZonedDateTime, guessesClose: String? ): ZonedDateTime {
        return if (guessesClose == null) {
           getDefaultCloseTime(startDate)
        } else {
            timeParser.parseDate(guessesClose)
        }
    }

    private fun getDefaultCloseTime(deliveryStart: ZonedDateTime): ZonedDateTime {
        val potentialDeadline = timeParser.toZoneDateTime(DateTime.now()).plusHours(1)
        return if (potentialDeadline >= deliveryStart) {
            deliveryStart.minusMinutes(5)
        } else {
            potentialDeadline
        }
    }
}