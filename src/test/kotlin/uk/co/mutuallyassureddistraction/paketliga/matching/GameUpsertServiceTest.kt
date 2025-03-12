package uk.co.mutuallyassureddistraction.paketliga.matching

import dev.kord.core.entity.Member
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.builders.ExtensibleBotBuilder
import io.mockk.every
import io.mockk.mockk
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.DEFAULT_CARRIER
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.UpdateGuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GameValidator

class GameUpsertServiceTest {

    private lateinit var target: GameUpsertService
    private val gameValidator: GameValidator = mockk<GameValidator>()
    private val activeGame = mockk<Game>()
    private val bot = mockk<ExtensibleBot>()

    private val guessWindow =
        GuessWindow(
            startTime = ZonedDateTime.parse("2024-10-15T19:00:00Z"),
            endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
        )

    private val updateGuessWindow =
        UpdateGuessWindow(
            startTime = ZonedDateTime.parse("2024-10-15T19:00:00Z"),
            endTime = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            guessDeadline = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
        )

    @BeforeEach
    fun setUp() {
        val gameDao = mockk<GameDao>()
        val guessFinderService = mockk<GuessFinderService>()
        every { gameDao.createGame(any()) } returns getGameStub()
        every { gameDao.findActiveGameById(1) } returns activeGame
        every { gameDao.updateGameTimes(any(), any(), any(), any(), any()) } returns getUpdatedGameStub()
        every { gameValidator.validateGameCreate(any()) } returns null
        every { gameValidator.validateGameUpdate(any(), any(), any(), any()) } returns null
        every { bot.settings } returns ExtensibleBotBuilder()
        every { bot.eventPublisher } returns MutableSharedFlow()

        val guessesResponse =
            arrayListOf(
                FindGuessesResponse(
                    guessId = 1,
                    gameId = 1,
                    userId = "Z",
                    guessTime = ZonedDateTime.parse("2023-04-10T10:00:00.000Z[Europe/London]"),
                )
            )
        every { guessFinderService.findGuesses(any(), any()) } returns guessesResponse

        target = GameUpsertService(gameDao, guessFinderService, gameValidator)
    }

    @DisplayName("createGame() if an error message is returned from validator return it")
    @Test
    fun returnsErrorMessageWhenCreatingGame() = runTest {
        val expectedString = "A failure"
        every { gameValidator.validateGameCreate(any()) } returns expectedString
        val returnedString = target.createGame(null, guessWindow, DEFAULT_CARRIER, "1234", null, "ZLX", bot)
        assertEquals(expectedString, returnedString)
    }

    @DisplayName("createGame() will return string with gameName and member mentioned if both values are not null")
    @Test
    fun returnStringWithNonNullGameNameAndMember() = runTest {
        val member = mockk<Member>()
        every { member.mention } returns "Z"
        val gameName = "Random Amazon package"

        val returnedString = target.createGame(gameName, guessWindow, DEFAULT_CARRIER, "1234", member, "ZLX", bot)
        val expectedString =
            ":postal_horn: Random Amazon package (#1) | Z's package is arriving between" +
                " Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(expectedString, returnedString)
    }

    @DisplayName(
        "createGame() will return string with default 'Game' string and username if game name and member are null"
    )
    @Test
    fun returnStringWithNullGameNameAndMember() = runTest {
        val returnedString = target.createGame(null, guessWindow, DEFAULT_CARRIER, "1234", null, "ZLX", bot)
        val expectedString =
            ":postal_horn: Game (#1) | ZLX's package is arriving between " +
                "Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(expectedString, returnedString)
    }

    @DisplayName("createGame() will return string with carrier if there is one provided")
    @Test
    fun returnStringWithCarrier() = runTest {
        val member = mockk<Member>()
        every { member.mention } returns "Z"
        val gameName = "Random Amazon package"

        val returnedString = target.createGame(gameName, guessWindow, "Amazon", "1234", member, "ZLX", bot)
        val expectedString =
            ":postal_horn: Random Amazon package (#1) | Z's Amazon package is arriving between" +
                " Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(expectedString, returnedString)
    }

    @DisplayName("updateGame() if validation fails return validation message")
    @Test
    fun returnUpdateStringOfFailure() {
        val member = mockk<Member>()
        every { member.mention } returns "@OhDearMoshe"

        val expectedString = "A big failure"
        every { gameValidator.validateGameUpdate(any(), any(), any(), any()) } returns expectedString
        val (updateString, _) =
            target.updateGame(1, "OhDear", member, updateGuessWindow, DEFAULT_CARRIER, "OhDearMoshe")

        assertEquals(updateString[0], expectedString)
    }

    @DisplayName("updateGame() will return updated game string and correct user IDS")
    @Test
    fun returnStringWithUpdatedGameInfo() {
        val member = mockk<Member>()
        every { member.mention } returns "@OhDearMoshe"
        val (updateString, userIds) =
            target.updateGame(1, "OhDear", member, updateGuessWindow, DEFAULT_CARRIER, "OhDearMoshe")

        val expectedString =
            ":postal_horn: #1 has been updated | @OhDearMoshe's package is now arriving between" +
                " Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(updateString[0], expectedString)
        assertEquals(userIds[0], "Z")
    }

    @DisplayName("updateGame() will return updated game string with carrier if not null of default")
    @Test
    fun returnStringWithCarrierIfNotNull() {
        val member = mockk<Member>()
        every { member.mention } returns "@OhDearMoshe"
        val (updateString, userIds) = target.updateGame(1, "OhDear", member, updateGuessWindow, "Amazon", "OhDearMoshe")

        val expectedString =
            ":postal_horn: #1 has been updated | @OhDearMoshe's Amazon package is now arriving between" +
                " Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(updateString[0], expectedString)
        assertEquals(userIds[0], "Z")
    }

    @DisplayName("updateGame() will return updated game string with user name if member is null")
    @Test
    fun returnStringWithUpdatedGameInfoIfMemberNull() {
        val member = mockk<Member>()
        every { member.mention } returns "@OhDearMoshe"
        val (updateString, userIds) =
            target.updateGame(1, "OhDear", null, updateGuessWindow, DEFAULT_CARRIER, "OhDearMoshe")

        val expectedString =
            ":postal_horn: #1 has been updated | OhDearMoshe's package is now arriving between " +
                "Tue 15 Oct 19:00 and Tue 15 Oct 20:00. Guesses accepted until Tue 15 Oct 18:00"
        assertEquals(updateString[0], expectedString)
        assertEquals(userIds[0], "Z")
    }

    @DisplayName("updateGame() will return just the carrier if only the carrier has been updated ")
    @Test
    fun returnStringWithJustCarrier() {
        val member = mockk<Member>()
        every { member.mention } returns "@OhDearMoshe"
        val (updateString, userIds) =
            target.updateGame(1, "OhDear", member, UpdateGuessWindow(null, null, null), "Amazon", "OhDearMoshe")

        val expectedString = ":postal_horn: #1 has been updated | @OhDearMoshe's package is now delivered by Amazon"
        assertEquals(updateString[0], expectedString)
        assertEquals(userIds[0], "Z")
    }

    private fun getUpdatedGameStub() =
        Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = guessWindow.startTime,
            windowClose = guessWindow.endTime,
            guessesClose = guessWindow.guessDeadline,
            deliveryTime = null,
            userId = "Z",
            gameActive = true,
        )

    private fun getGameStub() =
        Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.now().withHour(15).withMinute(0),
            windowClose = ZonedDateTime.now().withHour(19).withMinute(0),
            guessesClose = ZonedDateTime.now().withHour(14).withMinute(0),
            deliveryTime = null,
            userId = "Z",
            gameActive = true,
        )
}
