package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game

class VoidGameServiceTest {
    private lateinit var target: VoidGameService
    val gameDao = mockk<GameDao>()

    @BeforeEach
    fun setUp() {
        target = VoidGameService(gameDao)
    }

    @DisplayName("cullExpiredGames() will cull games that finished over 24 hours ago")
    @Test
    fun cullExpiredGamesCullsOldGames() {
        val expiredTime = ZonedDateTime.now().minusDays(1).minusMinutes(5)
        val expiredGame = createCullableGameStub(expiredTime)
        every { gameDao.findActiveGames(any(), any()) }.returns(listOf(expiredGame))
        every { gameDao.voidGameById(1, "Game became stale and expired") }.returns(expiredGame)

        target.cullExpiredGames()
        verify { gameDao.voidGameById(1, "Game became stale and expired") }
    }

    @DisplayName("cullExpiredGames() will not cull games end less than 24 hours ago")
    @Test
    fun cullExpiredGamesKeepsFreshGames() {
        val expiredTime = ZonedDateTime.now().minusHours(23)
        every { gameDao.findActiveGames(any(), any()) }.returns(listOf(createCullableGameStub(expiredTime)))
        target.cullExpiredGames()
        verify(exactly = 0) { gameDao.voidGameById(1, "Game became stale and expired") }
    }

    @DisplayName("voidGame() will return an error message if unable to find a game")
    @Test
    fun voidGameWillReturnErrorIfGameNotFound() {
        every { gameDao.findActiveGameById(1) }.returns(null)

        val response = target.voidGame(1, "No reason", "Zlaxxer")
        assertEquals("Game 1 was not found", response)
        verify(exactly = 0) { gameDao.voidGameById(any(), any()) }
    }

    @DisplayName("voidGame() will not let another user cancel anothers game")
    @Test
    fun voidGameUserTryingToCancelAnotherUsersGameWillReturnError() {
        every { gameDao.findActiveGameById(1) }.returns(createGameStub())

        val response = target.voidGame(1, "No reason", "OhDearMoshe")
        assertEquals("Mr Pump prevents you from interfering with another game", response)
        verify(exactly = 0) { gameDao.voidGameById(any(), any()) }
    }

    @DisplayName("voidGame() will let a user who owns the game void the game")
    @Test
    fun voidGameWillLetCorrectUserVoidAGame() {
        val game = createGameStub()
        every { gameDao.findActiveGameById(1) }.returns(game)
        every { gameDao.voidGameById(1, "A good reason") }.returns(game)
        val response = target.voidGame(1, "A good reason", "Zlaxxer")
        assertEquals("Game 1 has been voided", response)
        verify { gameDao.voidGameById(1, "A good reason") }
    }

    private fun createGameStub() = createCullableGameStub(ZonedDateTime.now())

    private fun createCullableGameStub(endWindow: ZonedDateTime): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.now(),
            windowClose = endWindow,
            guessesClose = ZonedDateTime.now(),
            deliveryTime = null,
            userId = "Zlaxxer",
            gameActive = true,
        )
    }
}
