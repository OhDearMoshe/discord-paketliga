package uk.co.mutuallyassureddistraction.paketliga.matching.time

import java.time.ZonedDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game

class GuessNudgerTest {
    val target = GuessNudger()
    val guessTime = GuessTime(ZonedDateTime.parse("2025-07-15T14:12:12.000Z"))

    @DisplayName("nudgeGuessToDeliveryDay() returns guess time if game is null")
    @Test
    fun ifGameIsNullReturnGuess() {
        val result = target.nudgeGuessToDeliveryDay(null, guessTime)
        assertEquals(guessTime, result)
    }

    @DisplayName("nudgeGuessToDeliveryDay() returns guess time if game spans multiple days")
    @Test
    fun ifGameWindowStretchesMultipleDaysReturnsGuess() {
        val game =
            createGame(ZonedDateTime.parse("2025-07-16T14:00:00.000Z"), ZonedDateTime.parse("2025-07-17T18:00:00.000Z"))
        val result = target.nudgeGuessToDeliveryDay(game, guessTime)
        assertEquals(guessTime, result)
    }

    @DisplayName("nudgeGuessToDeliveryDay() nudges game to guess day")
    @Test
    fun ifGameWindowSameDaysReturnNudgedGuess() {
        val game =
            createGame(ZonedDateTime.parse("2025-07-16T14:00:00.000Z"), ZonedDateTime.parse("2025-07-16T18:00:00.000Z"))
        val result = target.nudgeGuessToDeliveryDay(game, guessTime)
        assertEquals(ZonedDateTime.parse("2025-07-16T14:12:12.000Z"), result.guessTime)
    }

    private fun createGame(start: ZonedDateTime, end: ZonedDateTime): Game {
        return Game(
            gameName = "Testing",
            windowStart = start,
            windowClose = end,
            guessesClose = start,
            userId = "SomePerson",
            gameActive = true,
        )
    }
}
