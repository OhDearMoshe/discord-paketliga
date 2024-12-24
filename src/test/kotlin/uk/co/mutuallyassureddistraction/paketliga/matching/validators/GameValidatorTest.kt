package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import kotlin.test.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.UpdateGuessWindow

class GameValidatorTest {

    val target = GameValidator()

    @DisplayName("validateGameCreate() returns null if window is valid")
    @Test
    fun ifAllIsWellReturnNull() {
        val now = ZonedDateTime.now()
        val guessWindow =
            GuessWindow(startTime = now.plusHours(10), endTime = now.plusHours(11), guessDeadline = now.plusHours(9))
        assertNull(target.validateGameCreate(guessWindow))
    }

    @DisplayName("validateGameCreate() if start is equal to end then return error message")
    @Test
    fun ifStartEqualToEndReturnErrorMessage() {
        val guessWindow =
            GuessWindow(
                startTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
            )
        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameCreate(guessWindow),
        )
    }

    @DisplayName("validateGameCreate() if start is after the end then return error message")
    @Test
    fun ifStartAfterEndReturnErrorMessage() {
        val guessWindow =
            GuessWindow(
                startTime = ZonedDateTime.parse("2024-10-15T20:00:01Z"),
                endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
            )
        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameCreate(guessWindow),
        )
    }

    @DisplayName("validateGameCreate() if the guess deadline is in the past return an error message")
    @Test
    fun ifGuessDeadlineInThePastReturnErrorMessage() {
        val guessWindow =
            GuessWindow(
                startTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                endTime = ZonedDateTime.parse("2024-10-15T21:00:00Z"),
                guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
            )
        assertEquals(
            "<:pikachu:918170411605327924> Deadline for guesses can't be in the past.",
            target.validateGameCreate(guessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if game null return error message")
    @Test
    fun ifGameNullReturnMessage() {
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = null)
        assertEquals(
            "Inactive or invalid game ID. Double-check and try again",
            target.validateGameUpdate(null, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if userId does not match existing game then error")
    @Test
    fun ifUserIdsDoNotMatch() {
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = null)
        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                guessesClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                deliveryTime = null,
                userId = "321",
                gameActive = true,
            )
        assertEquals(
            "Mr Pump stops you from interfering with another persons mail",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if game is already over then error")
    @Test
    fun ifGameIsOver() {
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = null)
        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                guessesClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                deliveryTime = null,
                userId = "123",
                gameActive = false,
            )
        assertEquals(
            "Game (already) over, man. Should have sent this update first class",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if no update then error")
    @Test
    fun ifNoUpdate() {
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = null)
        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                guessesClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        assertEquals(
            "<:thonk:344120216227414018> You didn't change anything",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if start time is updated to end time fail")
    @Test
    fun ifNoStartTimeToEndThenError() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow = UpdateGuessWindow(startTime = game.windowClose, endTime = null, guessDeadline = null)

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if start time is updated to after end time fail")
    @Test
    fun ifNoStartTimeToAfterEndThenError() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = game.windowClose.plusSeconds(1), endTime = null, guessDeadline = null)

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated start time is same as updated end time fail")
    @Test
    fun ifNewStartAndEndTimeSameDontMatchFail() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(
                startTime = game.windowClose.plusHours(1),
                endTime = game.windowClose.plusHours(1),
                guessDeadline = null,
            )

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated start time is after the updated end time fail")
    @Test
    fun ifNewStartAferNewEndTimeSameFail() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(
                startTime = game.windowClose.plusHours(1).plusSeconds(1),
                endTime = game.windowClose.plusHours(1),
                guessDeadline = null,
            )

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated end is same as start fail")
    @Test
    fun updateEndSameAsStartTime() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = game.windowStart, guessDeadline = null)

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated end is before start fail")
    @Test
    fun updateEndBeforeStartTime() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = null, endTime = game.windowStart.minusSeconds(1), guessDeadline = null)

        assertEquals(
            "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated deadline is same as start fail")
    @Test
    fun updateDeadlineSameAsStart() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = game.windowStart)

        assertEquals(
            "<:ohno:760904962108162069> Deadline for guesses must be before the delivery window opens",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated deadline is after start fail")
    @Test
    fun updateDeadlineBeforeStart() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = game.windowStart.plusSeconds(1))

        assertEquals(
            "<:ohno:760904962108162069> Deadline for guesses must be before the delivery window opens",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if updated deadline is in past fail")
    @Test
    fun updateDeadlineToPast() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow = UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = now.minusSeconds(1))

        assertEquals(
            "<:pikachu:918170411605327924> Deadline for guesses can't be in the past.",
            target.validateGameUpdate(game, "123", updateGuessWindow),
        )
    }

    @DisplayName("validateGameUpdate() if valid start update return null")
    @Test
    fun updateValidStart() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = game.windowStart.plusMinutes(30), endTime = null, guessDeadline = null)

        assertNull(target.validateGameUpdate(game, "123", updateGuessWindow))
    }

    @DisplayName("validateGameUpdate() if valid end update return null")
    @Test
    fun updateValidEnd() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = null, endTime = game.windowClose.plusMinutes(30), guessDeadline = null)

        assertNull(target.validateGameUpdate(game, "123", updateGuessWindow))
    }

    @DisplayName("validateGameUpdate() if valid deadline update return null")
    @Test
    fun updateValidDeadline() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(startTime = null, endTime = null, guessDeadline = game.guessesClose.plusMinutes(30))

        assertNull(target.validateGameUpdate(game, "123", updateGuessWindow))
    }

    @DisplayName("validateGameUpdate() if valid update to all fields return null")
    @Test
    fun updateValidAllFields() {
        val now = ZonedDateTime.now()

        val game =
            Game(
                gameId = 1,
                gameName = "Testing Game",
                windowStart = now.plusHours(10),
                windowClose = now.plusHours(11),
                guessesClose = now.plusHours(9),
                deliveryTime = null,
                userId = "123",
                gameActive = true,
            )
        val updateGuessWindow =
            UpdateGuessWindow(
                startTime = game.windowStart.plusMinutes(30),
                endTime = game.windowClose.plusMinutes(30),
                guessDeadline = game.guessesClose.plusMinutes(30),
            )

        assertNull(target.validateGameUpdate(game, "123", updateGuessWindow))
    }
}
