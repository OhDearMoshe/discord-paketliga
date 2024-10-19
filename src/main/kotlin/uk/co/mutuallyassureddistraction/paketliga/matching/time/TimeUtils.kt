package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm")

fun toUserFriendlyString(time: ZonedDateTime): String {
    return time.format(dtf)
}