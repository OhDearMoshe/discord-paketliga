package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import java.sql.SQLException
import java.time.ZonedDateTime
import kotlin.test.*
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GuessValidator

class GuessUpsertServiceTest {
    private lateinit var target: GuessUpsertService

    private val mention = "@OhDearMoshe"
    private val guessTime = GuessTime(ZonedDateTime.parse("2024-10-15T19:00:00Z"))

    @DisplayName("guessGame() will create a guess successfully")
    @Test
    fun returnCreateGuessWithSuccessTrue() {
        val guessDao = mockk<GuessDao>()
        every { guessDao.createGuess(any()) } returns Unit
        val gameDao = mockk<GameDao>()
        val guessValidator = mockk<GuessValidator>()
        every { guessValidator.validateGuess(any(), any(), any(), any()) } returns null
        every { gameDao.findActiveGameById(any()) } returns getGameStub()
        target = GuessUpsertService(guessDao, gameDao, guessValidator)

        val response = target.guessGame(1, guessTime, "Z", mention)
        val expectedString = "<:sickos:918170456190775348> @OhDearMoshe has guessed Tue 15 Oct 19:00 for game ID #1"
        assertEquals(expectedString, response)
    }

    @DisplayName("guessGame() will create a guess successfully with a star if its 11:11")
    @Test
    fun returnCreateGuessWithSuccessTrueMakeAWish() {
        val wishingTime = GuessTime(ZonedDateTime.parse("2024-10-15T11:11:00Z"))
        val guessDao = mockk<GuessDao>()
        every { guessDao.createGuess(any()) } returns Unit
        val gameDao = mockk<GameDao>()
        val guessValidator = mockk<GuessValidator>()
        every { guessValidator.validateGuess(any(), any(), any(), any()) } returns null
        every { gameDao.findActiveGameById(any()) } returns getGameStub()
        target = GuessUpsertService(guessDao, gameDao, guessValidator)

        val response = target.guessGame(1, wishingTime, "Z", mention)
        val expectedString = ":stars: @OhDearMoshe has guessed Tue 15 Oct 11:11 for game ID #1"
        assertEquals(expectedString, response)
    }

    @DisplayName("guessGame() will fail to create a guess because failed validation")
    @Test
    fun returnCreateGuessWithFailedMessageDueToFailedValidation() {
        val guessDao = mockk<GuessDao>()
        val sqlException = SQLException("Guess time is not between start and closing window range of the game", "ERRA1")
        val exception = UnableToExecuteStatementException(sqlException, null)
        every { guessDao.createGuess(any()) } throws exception
        val gameDao = mockk<GameDao>()
        every { gameDao.findActiveGameById(any()) } returns getGameStub()
        val guessValidator = mockk<GuessValidator>()
        every { guessValidator.validateGuess(any(), any(), any(), any()) } returns "Error message"
        target = GuessUpsertService(guessDao, gameDao, guessValidator)

        val response = target.guessGame(4, guessTime, "Z", mention)
        val expectedString = "Error message"
        assertEquals(expectedString, response)
    }

    @DisplayName("guessGame() will fail to create a guess because of duplicate guess time")
    @Test
    fun returnCreateGuessWithFailedMessageDueToDuplicateGuessTime() {
        val guessDao = mockk<GuessDao>()
        val sqlException = SQLException("Guess time has already exist", "23505")
        val exception = UnableToExecuteStatementException(sqlException, null)
        every { guessDao.createGuess(any()) } throws exception
        val gameDao = mockk<GameDao>()
        every { gameDao.findActiveGameById(any()) } returns getGameStub()
        val guessValidator = mockk<GuessValidator>()
        every { guessValidator.validateGuess(any(), any(), any(), any()) } returns null
        target = GuessUpsertService(guessDao, gameDao, guessValidator)

        val response = target.guessGame(2, guessTime, "Z", mention)
        val expectedString = "*\\*womp-womp*\\* Your guess isn't within the delivery window"
        assertEquals(expectedString, response)
    }

    @DisplayName("guessGame() will fail to create a guess because guess time is not between guess window time")
    @Test
    fun returnCreateGuessWithFailedMessageDueToGuessTimeOutOfRange() {
        val guessDao = mockk<GuessDao>()
        val sqlException = SQLException("Guess time is not between start and closing window range of the game", "ERRA1")
        val exception = UnableToExecuteStatementException(sqlException, null)
        every { guessDao.createGuess(any()) } throws exception
        val gameDao = mockk<GameDao>()
        every { gameDao.findActiveGameById(any()) } returns getGameStub()
        val guessValidator = mockk<GuessValidator>()
        every { guessValidator.validateGuess(any(), any(), any(), any()) } returns null
        target = GuessUpsertService(guessDao, gameDao, guessValidator)

        val response = target.guessGame(4, guessTime, "Z", mention)
        val expectedString = "*\\*womp-womp*\\* Game ID #4 is not valid or is no longer active"
        assertEquals(expectedString, response)
    }

    private fun getGameStub() =
        Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.now().withHour(11).withMinute(0),
            windowClose = ZonedDateTime.now().withHour(19).withMinute(0),
            guessesClose = ZonedDateTime.now().withHour(18).withMinute(0),
            deliveryTime = null,
            userId = "Z",
            gameActive = true,
        )
}
