package uk.co.mutuallyassureddistraction.paketliga.dao.entity

data class Point(
    val pointId: Int? = null,
    val userId: String,
    val played: Int,
    val won: Int = 0,
    val lost: Int = 0,
    val drawn: Int = 0,
    val bonus: Int = 0,
    val totalPoint: Float,
)
