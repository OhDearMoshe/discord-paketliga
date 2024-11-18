package uk.co.mutuallyassureddistraction.paketliga.matching.results

import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess

data class GameResult(
    val winners: List<Guess>,
    val losers: List<Guess>,
    val awardBonusPoint: Boolean,
    val wasDraw: Boolean,
)
