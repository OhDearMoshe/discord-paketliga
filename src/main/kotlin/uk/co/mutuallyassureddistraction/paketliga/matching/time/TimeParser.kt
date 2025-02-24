package uk.co.mutuallyassureddistraction.paketliga.matching.time

import com.zoho.hawking.HawkingTimeParser
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TimeParser {

    private val parser = HawkingTimeParser()
    private val hawkingConfiguration = HawkingConfiguration()

    init {
        hawkingConfiguration.timeZone = ZoneId.systemDefault().toString()
        hawkingConfiguration.minuteSpan = 1
    }

    fun parseDate(dateString: String): ZonedDateTime {
        // I am not proud of this but it coerces it to be at the start of the day
        // so that simple times work a bit better
        val date = Date()
        date.hours = 2
        date.minutes = 0
        val parsedDate = parser.parse(dateString, date, hawkingConfiguration, "eng")
        val dateText = parsedDate.parserOutputs.first().dateRange.startDateFormat
        return fromParsedHawkingString(dateText).withSecond(0)
    }
}
