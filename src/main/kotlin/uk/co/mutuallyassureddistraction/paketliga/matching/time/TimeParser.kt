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
        val parsedDate = parser.parse(dateString, Date(), hawkingConfiguration, "eng")
        val dateText = parsedDate.parserOutputs.first().dateRange.startDateFormat
        return fromParsedHawkingString(dateText).withSecond(0)
    }
}
