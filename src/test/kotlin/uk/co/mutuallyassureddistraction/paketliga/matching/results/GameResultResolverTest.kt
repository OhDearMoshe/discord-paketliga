package uk.co.mutuallyassureddistraction.paketliga.matching.results

import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GameResultResolverTest {
    val target = GameResultResolver()

    @DisplayName("findWinners() will find the closest guess if there is only one closest guess before delivery time")
    @Test
    fun singleClosestGuessReturnedBefore() {
        val closestGuess = buildGuess("2023-03-03T20:52:00Z")
        val losingGuesses = listOf(buildGuess("2023-03-03T20:50:00Z"), buildGuess("2023-03-03T21:12:00Z"))
        val results = target.findWinners(buildGame(), losingGuesses + closestGuess)
        assertEquals(
            results,
            GameResult(
                winners = listOf(closestGuess),
                losers = losingGuesses,
                awardBonusPoint = false,
                wasDraw = false
            )
        )
    }

    @DisplayName("findWinners() will find the closest guess if there is only one closest guess after delivery time")
    @Test
    fun singleClosestGuessReturnedAfter() {
        val closestGuess = buildGuess("2023-03-03T21:02:00Z")
        val losingGuesses = listOf(buildGuess("2023-03-03T20:50:00Z"), buildGuess("2023-03-03T21:12:00Z"))
        val results = target.findWinners(buildGame(), losingGuesses + closestGuess)
        assertEquals(
            results,
            GameResult(
                winners = listOf(closestGuess),
                losers = losingGuesses,
                awardBonusPoint = false,
                wasDraw = false
            )
        )
    }

    @DisplayName(
        "findWinners() will find more than one guess if there is are two guesses of equal distance either side of delivery time"
    )
    @Test
    fun guessesOfEqualDistanceBothReturned() {
        val closestGuessBefore = buildGuess("2023-03-03T20:52:00Z")
        val closestGuessAfter = buildGuess("2023-03-03T21:08:00Z")
        val losingGuesses =
            listOf(
                buildGuess("2023-03-03T20:50:00Z"),
                buildGuess("2023-03-03T21:12:00Z"),
            )
        val results = target.findWinners(buildGame(), losingGuesses + closestGuessBefore + closestGuessAfter)
        assertEquals(
            results,
            GameResult(
                winners = listOf(closestGuessBefore, closestGuessAfter),
                losers = losingGuesses,
                awardBonusPoint = false,
                wasDraw = true
            )
        )
    }

    @DisplayName("findWinners() will return a guess that matches the delivery time with a bonus point")
    @Test
    fun guessThatMatchesDeliveryTimeIsReturned() {
        val closestGuess = buildGuess("2023-03-03T21:00:00Z")
        val losingGuesses = listOf(
            buildGuess("2023-03-03T20:52:00Z"),
            buildGuess("2023-03-03T20:50:00Z"),
            buildGuess("2023-03-03T21:12:00Z"),
        )
        val results = target.findWinners(buildGame(), losingGuesses + closestGuess)
        assertEquals(
            results,
            GameResult(
                winners = listOf(closestGuess),
                losers = losingGuesses,
                awardBonusPoint = true,
                wasDraw = false
            )
        )
    }

    private fun buildGuess(guessTime: String) =
        Guess(guessId = 1, gameId = 1, userId = "PostMasterGeneral", guessTime = ZonedDateTime.parse(guessTime))

    private fun buildGame() =
        Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.now(),
            windowClose = ZonedDateTime.now(),
            guessesClose = ZonedDateTime.now(),
            deliveryTime = ZonedDateTime.parse("2023-03-03T21:00:00Z"),
            userId = "SomeHumanMaybe",
            gameActive = true,
        )
}
