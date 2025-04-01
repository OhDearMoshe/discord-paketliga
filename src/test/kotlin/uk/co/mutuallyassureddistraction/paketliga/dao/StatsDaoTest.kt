package uk.co.mutuallyassureddistraction.paketliga.dao

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.GameCreated
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

    @DisplayName("getGameCreatedSortedByCountDesc() will successfully return userId and count of game created")
    @Test
    fun canSuccessfullyGetGameCreatedSortedByCountDesc() {
        val searchedStats: List<GameCreated> = target.getGameCreatedSortedByCountDesc()
        assertEquals(searchedStats.first().userId, "Z")
        assertEquals(searchedStats.first().gameCount, 1)
        assertEquals(searchedStats.first().mostCarrier, "N/A")
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
                gameActive = true,
            )
        gameDao.createGame(game)
    }
}