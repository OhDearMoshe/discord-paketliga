package uk.co.mutuallyassureddistraction.paketliga.matching

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GameTimeParserService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.TimeParser

class GameTimeParserServiceTest {

    val target = GameTimeParserService(TimeParser())
    private val resultFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE dd MMM HH:mm")
    private val inputFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    @DisplayName("parseGameTime() A guess with a defined start, end and deadline should create a guessWindow")
    @Test
    fun simpleGuessWithDeadlineShouldReturnAsExpected() {
        val startTime = "15/10/2024 19:00"
        val endTime = "15/10/2024 22:00"
        val deadline = "15/10/2024 18:00"

        val result = target.parseGameTime(startTime, endTime, deadline)
        assertEquals("Tue 15 Oct 19:00", result.startAsHumanFriendlyString())
        assertEquals("Tue 15 Oct 22:00", result.endAsHumanFriendlyString())
        assertEquals("Tue 15 Oct 18:00", result.deadlineAsHumanFriendlyString())
    }

    @DisplayName(
        "parseGameTime() A guess with a defined start and end but no deadline should default the deadline to an hour from now"
    )
    @Test
    fun missingDeadlineWillDefaultToAnHourFromNow() {
        val startDate = LocalDateTime.now().plusHours(2)
        val endDate = LocalDateTime.now().plusHours(3)
        val expectedDeadline = LocalDateTime.now().plusHours(1)

        val result = target.parseGameTime(startDate.format(inputFormat), endDate.format(inputFormat), null)
        assertEquals(startDate.format(resultFormat), result.startAsHumanFriendlyString())
        assertEquals(endDate.format(resultFormat), result.endAsHumanFriendlyString())
        assertEquals(expectedDeadline.format(resultFormat), result.deadlineAsHumanFriendlyString())
    }

    @DisplayName(
        "parseGameTime() A guess with a defined start and end but no deadline should default to five minutes before the StartWindow if StartWindow is closer than an hour from now"
    )
    @Test
    fun missingDeadlineWillDefaultToFiveMinsBeforeStartWindow() {
        val startDate = LocalDateTime.now().plusMinutes(30)
        val endDate = LocalDateTime.now().plusHours(3)
        val expectedDeadline = LocalDateTime.now().plusMinutes(25)

        val result = target.parseGameTime(startDate.format(inputFormat), endDate.format(inputFormat), null)
        assertEquals(startDate.format(resultFormat), result.startAsHumanFriendlyString())
        assertEquals(endDate.format(resultFormat), result.endAsHumanFriendlyString())
        assertEquals(expectedDeadline.format(resultFormat), result.deadlineAsHumanFriendlyString())
    }

    @DisplayName("parseGameUpdateTime() A guess just a start returns only that")
    @Test
    fun testParseGameUpdateTimeForStart() {
        val time = "15/10/2024 19:00"
        val expectedTime = ZonedDateTime.parse("2024-10-15T19:00+01:00[Europe/London]")

        val result = target.parseGameUpdateTime(time, null, null)
        assertEquals(expectedTime, result.startTime)
        assertNull(result.endTime)
        assertNull(result.guessDeadline)
    }

    @DisplayName("parseGameUpdateTime() A guess just a end returns only that")
    @Test
    fun testParseGameUpdateTimeForEnd() {
        val time = "15/10/2024 19:00"
        val expectedTime = ZonedDateTime.parse("2024-10-15T19:00+01:00[Europe/London]")

        val result = target.parseGameUpdateTime(null, time, null)
        assertNull(result.startTime)
        assertEquals(expectedTime, result.endTime)
        assertNull(result.guessDeadline)
    }

    @DisplayName("parseGameUpdateTime() A guess just a guess deadline returns only that")
    @Test
    fun testParseGameUpdateTimeForDeadline() {
        val time = "15/10/2024 19:00"
        val expectedTime = ZonedDateTime.parse("2024-10-15T19:00+01:00[Europe/London]")

        val result = target.parseGameUpdateTime(null, null, time)
        assertNull(result.startTime)
        assertNull(result.endTime)
        assertEquals(expectedTime, result.guessDeadline)
    }

    @DisplayName("parseGameTime() A guess with seconds has the seconds part trimmed")
    @Test
    fun guessWithSecondsShouldHaveSecondsTrimmed() {
        val startTime = "15/10/2024 19:00:15"
        val endTime = "15/10/2024 22:00:30"
        val deadline = "15/10/2024 18:00:45"

        val result = target.parseGameTime(startTime, endTime, deadline)
        assertEquals(0, result.startTime.second)
        assertEquals(0, result.endTime.second)
        assertEquals(0, result.guessDeadline.second)
    }
}
