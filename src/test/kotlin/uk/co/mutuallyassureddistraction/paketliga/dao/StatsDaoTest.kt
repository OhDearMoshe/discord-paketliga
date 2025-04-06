package uk.co.mutuallyassureddistraction.paketliga.dao

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Carrier
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.UserGame
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class StatsDaoTest {
    private lateinit var target: StatsDao
    private lateinit var gameDao: GameDao
    private lateinit var testWrapper: DaoTestWrapper

    @BeforeEach
    fun setUp() {
        testWrapper = initTests()
        target = testWrapper.buildDao(StatsDao::class.java)
        gameDao = testWrapper.buildDao(GameDao::class.java)
        createGame()
    }

    @AfterEach
    fun tearDown() {
        testWrapper.stopContainers()
    }

    @DisplayName("getGamesCreatedByUsers() will successfully return userId and count of game created")
    @Test
    fun canSuccessfullyGetGamesCreatedByUsers() {
        val searchedStats: List<UserGame> = target.getGamesCreatedByUsers(1)
        assertEquals(searchedStats.first().userId, "Z")
        assertEquals(searchedStats.first().gameCount, 1)
    }

    @DisplayName("getMostPopularCarriers() will successfully return carrier and count of carriers")
    @Test
    fun canSuccessfullyGetMostPopularCarriers() {
        val searchedStats: List<Carrier> = target.getMostPopularCarriers(1)
        assertEquals(searchedStats.first().carrier, "N/A")
        assertEquals(searchedStats.first().carrierCount, 1)
    }

    @DisplayName("getCarriersWithMostVoidedGames() will successfully return carrier and count of most voided carriers")
    @Test
    fun canSuccessfullyGetCarriersWithMostVoidedGames() {
        val searchedStats: List<Carrier> = target.getCarriersWithMostVoidedGames(1)
        assertEquals(searchedStats.first().carrier, "N/A")
        assertEquals(searchedStats.first().carrierCount, 1)
    }

    @DisplayName("getUsersWithMostVoidedGames() will successfully return carrier and count of most voided carriers")
    @Test
    fun canSuccessfullyGetUsersWithMostVoidedGames() {
        val searchedStats: List<UserGame> = target.getUsersWithMostVoidedGames(1)
        assertEquals(searchedStats.first().userId, "Z")
        assertEquals(searchedStats.first().gameCount, 1)
    }

    private fun createGame() {
        val game =
            Game(
                gameId = 1,
                gameName = "A random game name for test",
                windowStart = ZonedDateTime.parse("2023-04-07T09:00:00.000Z[Europe/London]"),
                windowClose = ZonedDateTime.parse("2023-04-07T17:00:00.000Z[Europe/London]"),
                guessesClose = ZonedDateTime.parse("2023-04-07T12:00:00.000Z[Europe/London]"),
                deliveryTime = null,
                userId = "Z",
                carrier = "N/A",
                gameVoided = true,
                gameActive = true,
            )
        gameDao.createGame(game)
    }
}
