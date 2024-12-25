package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime

data class DeliveryTime(val deliveryTime: ZonedDateTime) {
    fun toHumanTime(): String = deliveryTime.toUserFriendlyString()
}
