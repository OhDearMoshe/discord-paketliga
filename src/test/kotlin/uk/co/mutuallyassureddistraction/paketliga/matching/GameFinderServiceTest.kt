package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game

class GameFinderServiceTest {
    private lateinit var target: GameFinderService
    private val expectedGame: Game = getGameStub()

    @BeforeEach
    fun setUp() {
        val gameDao = mockk<GameDao>()
        every { gameDao.findActiveGameById(any()) } returns expectedGame
        every { gameDao.findActiveGames(any(), any()) } returns listOf(expectedGame)
        target = GameFinderService(gameDao)
    }

    @DisplayName("findGames() with gameId param will return a game after searched using gameId")
    @Test
    fun returnListOfResponseWhenSearchingWithGameId() {
        val returnedList = target.findGames(null, null, 1)
        with(returnedList[0]) {
            assertEquals(gameId, expectedGame.gameId)
            assertEquals(userId, expectedGame.userId)
            assertEquals(windowStart, expectedGame.windowStart)
            assertEquals(windowClose, expectedGame.windowClose)
            assertEquals(guessesClose, expectedGame.guessesClose)
        }
    }

    @DisplayName("findGames() with gameName / userId param will return searched game")
    @Test
    fun returnListOfResponseWhenSearchingWithGameNameAndOrUserId() {
        val returnedList = target.findGames("Z", "testing", null)
        with(returnedList[0]) {
            assertEquals(gameId, expectedGame.gameId)
            assertEquals(userId, expectedGame.userId)
            assertEquals(windowStart, expectedGame.windowStart)
            assertEquals(windowClose, expectedGame.windowClose)
            assertEquals(guessesClose, expectedGame.guessesClose)
        }
    }

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
