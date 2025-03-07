package uk.co.mutuallyassureddistraction.paketliga.matching.time

class DeliveryTimeParser(private val timeParser: TimeParser) {
    fun parse(time: String): DeliveryTime = DeliveryTime(timeParser.parseEarlierDate(time))
}
