package uk.co.mutuallyassureddistraction.paketliga.matching.results

import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess

// TODO: Add losers and booleans into the tests
class GameResultResolverTest {
    val target = GameResultResolver()

    @DisplayName("findWinners() will find the closest guess if there is only one closest guess before delivery time")
    @Test
    fun singleClosestGuessReturnedBefore() {
        val closestGuess = buildGuess("2023-03-03T20:52:00Z")
        val guesses = listOf(closestGuess, buildGuess("2023-03-03T20:50:00Z"), buildGuess("2023-03-03T21:12:00Z"))
        val results = target.findWinners(buildGame(), guesses)
        assertEquals(1, results.winners.size)
        assertEquals(closestGuess, results.winners[0])
    }

    @DisplayName("findWinners() will find the closest guess if there is only one closest guess after delivery time")
    @Test
    fun singleClosestGuessReturnedAfter() {
        val closestGuess = buildGuess("2023-03-03T21:02:00Z")
        val guesses = listOf(closestGuess, buildGuess("2023-03-03T20:50:00Z"), buildGuess("2023-03-03T21:12:00Z"))
        val results = target.findWinners(buildGame(), guesses)
        assertEquals(1, results.winners.size)
        assertEquals(closestGuess, results.winners[0])
    }

    @DisplayName(
        "findWinners() will find more than one guess if there is are two guesses of equal distance either side of delivery time"
    )
    @Test
    fun guessesOfEqualDistanceBothReturned() {
        val closestGuessBefore = buildGuess("2023-03-03T20:52:00Z")
        val closestGuessAfter = buildGuess("2023-03-03T21:08:00Z")
        val guesses =
            listOf(
                closestGuessBefore,
                closestGuessAfter,
                buildGuess("2023-03-03T20:50:00Z"),
                buildGuess("2023-03-03T21:12:00Z"),
            )
        val results = target.findWinners(buildGame(), guesses)
        assertEquals(2, results.winners.size)
        assertEquals(closestGuessBefore, results.winners[0])
        assertEquals(closestGuessAfter, results.winners[1])
    }

    @DisplayName("findWinners() will return a guess that matches the delivery time")
    @Test
    fun guessThatMatchesDeliveryTimeIsReturned() {
        val closestGuess = buildGuess("2023-03-03T21:00:00Z")
        val guesses =
            listOf(
                closestGuess,
                buildGuess("2023-03-03T20:52:00Z"),
                buildGuess("2023-03-03T20:50:00Z"),
                buildGuess("2023-03-03T21:12:00Z"),
            )
        val results = target.findWinners(buildGame(), guesses)
        assertEquals(1, results.winners.size)
        assertEquals(closestGuess, results.winners[0])
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
