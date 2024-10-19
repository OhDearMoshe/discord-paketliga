package uk.co.mutuallyassureddistraction.paketliga.matching.results

import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

@Suppress("MagicNumber")
class GameResultResolver {
    fun findWinners(game: Game, guesses: List<Guess>): GameResult {
        var closestGuesses = mutableListOf<Guess>()
        val deliveryTime = game.deliveryTime
        var shortestDistance = 1000000000000000000L
        guesses.forEach {
            val currentDistance = it.guessTime.until(deliveryTime, ChronoUnit.SECONDS).absoluteValue
            if(currentDistance < shortestDistance) {
                shortestDistance = currentDistance
                closestGuesses = mutableListOf(it)
            } else if(currentDistance == shortestDistance) {
                closestGuesses.add(it)
            }
        }

        val losers = guesses.filter { !closestGuesses.contains(it) }.toList()
        val isDraw = closestGuesses.size == 2
        val awardBonusPoint = shortestDistance == 0L

        return GameResult(winners = closestGuesses,
            losers = losers,
            awardBonusPoint = awardBonusPoint,
            wasDraw = isDraw
            )
    }
}
