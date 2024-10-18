package uk.co.mutuallyassureddistraction.paketliga.matching

import com.zoho.hawking.HawkingTimeParser
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration
import org.joda.time.DateTime
import java.time.ZoneId
import java.util.*

class GameTimeParserService {

    //TODO: Externalise these to proper DI
    private val parser = HawkingTimeParser()
    private val hawkingConfiguration = HawkingConfiguration()

    init {
        hawkingConfiguration.timeZone = ZoneId.systemDefault().toString()
    }

    fun parseGameTime(startWindow: String, closeWindow: String, guessesClose: String?): GuessWindow {
        val startDate = parseDate(startWindow)
        val closeDate = parseDate(closeWindow)
        val guessDeadline = resolveCloseTime(startDate, guessesClose)

        return GuessWindow(
            startTime = startDate.toGregorianCalendar().toZonedDateTime(),
            endTime = closeDate.toGregorianCalendar().toZonedDateTime(),
            guessDeadline = guessDeadline.toGregorianCalendar().toZonedDateTime()
        )
    }

    fun parseGameUpdateTime(startWindow: String?, closeWindow: String?, guessesClose: String?): UpdateGuessWindow {
        val startDate = startWindow?.let { parseDate(it) }
        val closeDate = closeWindow?.let { parseDate(it) }
        val guessDeadline = guessesClose?.let { parseDate(it) }
        return UpdateGuessWindow(
            startTime = startDate?.toGregorianCalendar()?.toZonedDateTime(),
            endTime = closeDate?.toGregorianCalendar()?.toZonedDateTime(),
            guessDeadline = guessDeadline?.toGregorianCalendar()?.toZonedDateTime()
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
        val parsedDate = parser.parse(dateString, Date(), hawkingConfiguration, "eng")
        return parsedDate.parserOutputs[0].dateRange.start
    }
}