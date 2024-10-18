package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.matching.GuessWindow
import java.time.ZonedDateTime
import kotlin.test.Test

class GuessWindowValidatorTest {

    val target = GuessWindowValidator()

    @DisplayName("validateGuessWindow() returns null if window is valid")
    @Test
    fun ifAllIsWellReturnNull() {
        val now = ZonedDateTime.now()
        val guessWindow = GuessWindow(
            startTime = now.plusHours(10),
            endTime = now.plusHours(11) ,
            guessDeadline = now.plusHours(9)
        )
        assertNull(target.validateGuessWindow(guessWindow))
    }

    @DisplayName("validateGuessWindow() if start is equal to end then return error message")
    @Test
    fun ifStartEqualToEndReturnErrorMessage() {
        val guessWindow = GuessWindow(
            startTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z") ,
            guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z")
        )
        assertEquals("Start time must be before end time", target.validateGuessWindow(guessWindow))
    }

    @DisplayName("validateGuessWindow() if start is after the end then return error message")
    @Test
    fun ifStartAfterEndReturnErrorMessage() {
        val guessWindow = GuessWindow(
            startTime = ZonedDateTime.parse("2024-10-15T20:00:01Z"),
            endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z") ,
            guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z")
        )
        assertEquals("Start time must be before end time", target.validateGuessWindow(guessWindow))
    }

    @DisplayName("validateGuessWindow() if the guess deadline is in the past return an error message")
    @Test
    fun ifGuessDeadlineInThePastReturnErrorMessage() {
        val guessWindow = GuessWindow(
            startTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            endTime = ZonedDateTime.parse("2024-10-15T21:00:00Z") ,
            guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z")
        )
        assertEquals("Game can not be in the past", target.validateGuessWindow(guessWindow))
    }
}