package uk.co.mutuallyassureddistraction.paketliga.dao.entity

import java.time.ZonedDateTime

data class Win(
    val winId: Int? = null,
    val gameId: Int,
    val guessId: Int,
    val date: ZonedDateTime,
)