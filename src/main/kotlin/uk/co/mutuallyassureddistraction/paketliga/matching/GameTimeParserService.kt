package uk.co.mutuallyassureddistraction.paketliga.matching

import com.zoho.hawking.HawkingTimeParser
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.*

class GameTimeParserService {

    //TODO: Externalise these to proper DI
    private val parser = HawkingTimeParser()
    private val referenceDate = Date()
    private val hawkingConfiguration = HawkingConfiguration()
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yy HH:mm")

    init {
        hawkingConfiguration.timeZone = ZoneId.systemDefault().toString()
    }

    fun parseGameTime(startWindow: String, closeWindow: String, guessesClose: String?): GuessWindow {
        val startDate = parseDate(startWindow)
        val closeDate = parseDate(closeWindow)
        val guessDeadline = resolveCloseTime(startDate, guessesClose)

        return GuessWindow(
            startTime = startDate.toString(dateTimeFormatter),
            endTime = closeDate.toString(dateTimeFormatter),
            guessDeadline = guessDeadline.toString(dateTimeFormatter)
        )

    }

    private fun resolveCloseTime(startDate: DateTime, guessesClose: String? ): DateTime {
        return if (guessesClose == null) {
            getDefaultCloseTime(startDate)
        } else {
            parseDate(guessesClose)
        }
    }

    private fun getDefaultCloseTime(deliveryStart: DateTime): DateTime {
        val potentialDeadline = DateTime.now().plusHours(1)
        return if (potentialDeadline >= deliveryStart) {
            deliveryStart.minusMinutes(5)
        } else {
            potentialDeadline
        }
    }

    private fun parseDate(dateString: String): DateTime {
        val parsedDate = parser.parse(dateString, referenceDate, hawkingConfiguration, "eng")
        return parsedDate.parserOutputs[0].dateRange.start
    }
}