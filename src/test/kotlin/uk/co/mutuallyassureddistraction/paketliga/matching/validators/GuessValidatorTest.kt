package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class GuessValidatorTest {
    private val target = GuessValidator()
    private val windowStart = ZonedDateTime.now().plusHours(4)
    private val windowClose = ZonedDateTime.now().plusHours(8)
    private val guessesClose = ZonedDateTime.now().plusHours(1)
    private val game =
        Game(
            gameId = 2,
            gameName = "A second game",
            windowStart = windowStart,
            windowClose = windowClose,
            guessesClose = guessesClose,
            deliveryTime = null,
            userId = "Z",
            gameActive = true,
        )
    private val gameId = 1
    private val userId = "@OhDearMoshe"
    private val guessTime = GuessTime(windowStart.plusHours(1))

    @DisplayName("validateGuess() returns null if guess is valid")
    @Test
    fun ifAllIsWellReturnNull() {
        assertNull(target.validateGuess(game, gameId, userId, guessTime))
    }

    @DisplayName("validateGuess() returns error if the game is not active")
    @Test
    fun returnErrorIfGameIsNotActive() {
        assertEquals(
            "Guessing failed, there is no active game with game ID #1",
            target.validateGuess(null, gameId, userId, guessTime),
        )
    }

    @DisplayName("validateGuess() returns error if the user is trying to guess in their own game")
    @Test
    fun returnErrorIfGuessingInOwnGame() {
        assertEquals(
            "Mr Pump forbids you from guessing in your own game.",
            target.validateGuess(game, gameId, "Z", guessTime),
        )
    }

    @DisplayName("validateGuess() returns an error if the guessing window has closed for the game")
    @Test
    fun returnErrorIfGuessingWindowIsClosed() {
        val earlyClose = ZonedDateTime.now().minusHours(1)
        val gameWithEarlyClose = game.copy(guessesClose = earlyClose)
        assertEquals(
            "*\\*womp-womp*\\* Too late, the guessing window has closed for game ID #1",
            target.validateGuess(gameWithEarlyClose, gameId, userId, guessTime),
        )
    }

    @DisplayName("validateGuess() returns an error if the guess is before the start of the guessing window")
    @Test
    fun returnErrorIfGuessIsBeforeStartOfGuessingWindow() {
        val earlyGuess = GuessTime(windowStart.minusHours(1))
        assertEquals(
            "Guesses must be between ${windowStart.toUserFriendlyString()} and ${windowClose.toUserFriendlyString()}",
            target.validateGuess(game, gameId, userId, earlyGuess),
        )
    }

    @DisplayName("validateGuess()  returns an error if the guess is after the end of the guessing window")
    @Test
    fun returnErrorIfGuessIsAfterEndOfGuessingWindow() {
        val lateGuess = GuessTime(windowClose.plusHours(1))
        assertEquals(
            "Guesses must be between ${windowStart.toUserFriendlyString()} and ${windowClose.toUserFriendlyString()}",
            target.validateGuess(game, gameId, userId, lateGuess),
        )
    }
}
