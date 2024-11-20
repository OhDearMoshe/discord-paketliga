package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val toStringFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm")

fun toUserFriendlyString(time: ZonedDateTime): String {
    return time.format(toStringFormatter)
}

fun fromParsedHawkingString(dateTime: String): ZonedDateTime {
    val zonedId = ZoneId.of("Europe/London")
    val ldt = LocalDateTime.parse(dateTime)
    val instant = ldt.toInstant(zonedId.rules.getOffset(ldt))
    return ZonedDateTime.ofInstant(instant, zonedId)
}
