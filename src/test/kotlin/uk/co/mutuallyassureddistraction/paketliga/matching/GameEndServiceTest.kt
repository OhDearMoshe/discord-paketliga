package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import uk.co.mutuallyassureddistraction.paketliga.matching.results.GameResult
import uk.co.mutuallyassureddistraction.paketliga.matching.results.GameResultResolver
import uk.co.mutuallyassureddistraction.paketliga.matching.results.PointUpdaterService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.DeliveryTime
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GameEndServiceTest {
    private lateinit var target: GameEndService
    private val pointUpdaterService = mockk<PointUpdaterService>()
    private lateinit var gameResult: GameResult
    private val deliveryTime = DeliveryTime(ZonedDateTime.parse("2024-10-15T19:00:00Z"))

    @BeforeEach
    fun setUp() {
        val winningGuess = getWinningGuessStub()
        val losingGuess = getLosingGuessStub()
        val searchedGame = getGameStub()

        gameResult = GameResult(winners = listOf(winningGuess), losers = listOf(losingGuess), awardBonusPoint = false, wasDraw = false )

        val gameDao = mockk<GameDao>()
        every {gameDao.findActiveGameById(999)} returns null
        every {gameDao.findActiveGameById(0)} returns searchedGame
        every {gameDao.findActiveGameById(1)} returns getGameStubEarly()
        every {gameDao.findActiveGameById(2)} returns getGameStubLate()
        every {gameDao.findActiveGameById(3)} returns getGameStubLateSameDay()
        every {gameDao.findActiveGameById(4)} returns getGameStubEarlyAfterWindowClose()
        every {gameDao.finishGame(any(), any())} returns searchedGame
        every {gameDao.voidGameById(any())} returns searchedGame

        val guessDao = mockk<GuessDao>()
        every {guessDao.findGuessesByGameId(any())} returns arrayListOf(winningGuess, losingGuess)


        val gameResultResolver = mockk<GameResultResolver>()

        every {gameResultResolver.findWinners(any(), any()) } returns gameResult

        every { pointUpdaterService.applyPoints(any()) } returns Unit

        target = GameEndService(guessDao, gameDao, gameResultResolver, pointUpdaterService)
    }

    @DisplayName("endGame() will return no games found string and empty array when no game is found")
    @Test
    fun returnStringWithNoGamesFound() {
        val returned = target.endGame(999, deliveryTime)
        assertEquals(returned.first, "No games found.")
        assertNull(returned.second)
    }

    @DisplayName("endGame() will void game id early delivery")
    @Test
    fun willVoidGameIfBeforeDeliveryWindow() {
        val returned = target.endGame(1, deliveryTime)
        assertEquals(returned.first, "Delivery time is outside of delivery window game void")
        assertNull(returned.second)
    }

    @DisplayName("endGame() will void game if late delivery")
    @Test
    fun willVoidGameIAfterDeliveryWindow() {
        val returned = target.endGame(2, deliveryTime)
        assertEquals(returned.first, "Delivery time is outside of delivery window game void")
        assertNull(returned.second)
    }

    @DisplayName("endGame() will return null string and array of winning guesses")
    @Test
    fun returnNullStringWithWinners() {
        val returned = target.endGame(0, deliveryTime)
        assertEquals(returned.first, null)
        assertEquals(returned.second!!.winners.size, 1)
        assertEquals(returned.second!!.winners[0].userId, "Z")
    }

    @DisplayName("endGame() will return null string and array of winning guesses if late but same day delivery")
    @Test
    fun returnNullStringWithWinnersLateAndSameDay() {
        val returned = target.endGame(3, deliveryTime)
        assertEquals(returned.first, null)
        assertEquals(returned.second!!.winners.size, 1)
        assertEquals(returned.second!!.winners[0].userId, "Z")
    }

    @DisplayName("endGame() will return null string and array of winning guesses if early but still below guess close")
    @Test
    fun returnNullStringWithWinnersEarlyGuessClose() {
        val returned = target.endGame(4, deliveryTime)
        assertEquals(returned.first, null)
        assertEquals(returned.second!!.winners.size, 1)
        assertEquals(returned.second!!.winners[0].userId, "Z")
    }

    private fun getWinningGuessStub(): Guess {
        return Guess (
            guessId = 1,
            gameId = 1,
            userId = "Z",
            guessTime = ZonedDateTime.now().withHour(14).withMinute(38)
        )
    }

    private fun getLosingGuessStub(): Guess {
        return Guess (
            guessId = 2,
            gameId = 1,
            userId = "X",
            guessTime = ZonedDateTime.now().withHour(20).withMinute(38)
        )
    }

    private fun getGameStub(): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
            windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            guessesClose = ZonedDateTime.parse("2024-10-15T16:00:00Z"),
            deliveryTime = null,
            userId = "Z",
            gameActive = true
        )
    }

    private fun getGameStubEarly(): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.parse("2024-10-15T18:00:01Z"),
            windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            guessesClose = ZonedDateTime.parse("2024-10-15T19:00:01Z"),
            deliveryTime = null,
            userId = "Z",
            gameActive = true
        )
    }

    private fun getGameStubEarlyAfterWindowClose(): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.parse("2024-10-15T18:00:01Z"),
            windowClose = ZonedDateTime.parse("2024-10-15T20:00:00Z"),
            guessesClose = ZonedDateTime.parse("2024-10-15T19:00:00Z"),
            deliveryTime = null,
            userId = "Z",
            gameActive = true
        )
    }

    private fun getGameStubLate(): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.parse("2024-10-14T18:00:00Z"),
            windowClose = ZonedDateTime.parse("2024-10-14T18:59:59Z"),
            guessesClose = ZonedDateTime.parse("2024-10-14T16:00:00Z"),
            deliveryTime = null,
            userId = "Z",
            gameActive = true
        )
    }

    private fun getGameStubLateSameDay(): Game {
        return Game(
            gameId = 1,
            gameName = "Testing testing",
            windowStart = ZonedDateTime.parse("2024-10-15T18:00:00Z"),
            windowClose = ZonedDateTime.parse("2024-10-15T18:59:59Z"),
            guessesClose = ZonedDateTime.parse("2024-10-15T16:00:00Z"),
            deliveryTime = null,
            userId = "Z",
            gameActive = true
        )
    }
}