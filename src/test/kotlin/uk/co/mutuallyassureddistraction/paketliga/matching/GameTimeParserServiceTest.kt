package uk.co.mutuallyassureddistraction.paketliga.matching

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.DisplayName

class GameTimeParserServiceTest {

    val target = GameTimeParserService()
    private val resultFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd-MMM-yy HH:mm")
    private val inputFormat: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yy HH:mm")

    @DisplayName("parseGameTime() A guess with a defined start, end and deadline should create a guessWindow")
    @Test
    fun simpleGuessWithDeadlineShouldReturnAsExpected() {
        val startTime = "15/10/2024 19:00"
        val endTime = "15/10/2024 22:00"
        val deadline = "15/10/2024 18:00"

        val result = target.parseGameTime(startTime, endTime, deadline)
        assertEquals("15-Oct-24 19:00", result.startAsHumanFriendlyString())
        assertEquals("15-Oct-24 22:00", result.endAsHumanFriendlyString())
        assertEquals("15-Oct-24 18:00", result.deadlineAsHumanFriendlyString())
    }

    @DisplayName("parseGameTime() A guess with a defined start and end but no deadline should default the deadline to an hour from now")
    @Test
    fun missingDeadlineWillDefaultToAnHourFromNow() {
        val startDate = DateTime.now().plusHours(2)
        val endDate = DateTime.now().plusHours(3)
        val expectedDeadline = DateTime.now().plusHours(1);

        val result = target.parseGameTime(startDate.toString(inputFormat), endDate.toString(inputFormat), null)
        assertEquals(startDate.toString(resultFormat), result.startAsHumanFriendlyString())
        assertEquals(endDate.toString(resultFormat), result.endAsHumanFriendlyString())
        assertEquals(expectedDeadline.toString(resultFormat), result.deadlineAsHumanFriendlyString())
    }

    @DisplayName("parseGameTime() A guess with a defined start and end but no deadline should default to five minutes before the StartWindow if StartWindow is closer than an hour from now")
    @Test
    fun missingDeadlineWillDefaultToFiveMinsBeforeStartWindow() {
        val startDate = DateTime.now().plusMinutes(30)
        val endDate = DateTime.now().plusHours(3)
        val expectedDeadline = DateTime.now().plusMinutes(25);

        val result = target.parseGameTime(startDate.toString(inputFormat), endDate.toString(inputFormat), null)
        assertEquals(startDate.toString(resultFormat), result.startAsHumanFriendlyString())
        assertEquals(endDate.toString(resultFormat), result.endAsHumanFriendlyString())
        assertEquals(expectedDeadline.toString(resultFormat), result.deadlineAsHumanFriendlyString())
    }
}