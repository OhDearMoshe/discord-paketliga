package uk.co.mutuallyassureddistraction.paketliga.matching.time

import com.zoho.hawking.HawkingTimeParser
import com.zoho.hawking.datetimeparser.configuration.HawkingConfiguration
import org.joda.time.DateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TimeParser {

    private val parser = HawkingTimeParser()
    private val hawkingConfiguration = HawkingConfiguration()

    init {
        hawkingConfiguration.timeZone = ZoneId.systemDefault().toString()
    }

    fun parseDate(dateString: String): ZonedDateTime {
        val parsedDate = parser.parse(dateString, Date(), hawkingConfiguration, "eng")
        return parsedDate.parserOutputs[0].dateRange.start.toGregorianCalendar().toZonedDateTime()
    }

    // Yum Consistency
    fun toZoneDateTime(date: DateTime): ZonedDateTime {
        return date.toGregorianCalendar().toZonedDateTime()
    }
}