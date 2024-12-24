package uk.co.mutuallyassureddistraction.paketliga.matching

import io.mockk.every
import io.mockk.mockk
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess

class GuessFinderServiceTest {
    private lateinit var target: GuessFinderService
    private val expectedGuess: Guess = getGuessStub()

    @BeforeEach
    fun setUp() {
        val guessDao = mockk<GuessDao>()
        every { guessDao.findGuessByGuessId(any()) } returns expectedGuess
        every { guessDao.findGuessesByGameId(any()) } returns listOf(expectedGuess)
        target = GuessFinderService(guessDao)
    }

    @DisplayName("findGuesses() with gameId param will return list of guesses after searched using gameId")
    @Test
    fun returnListOfResponseWhenSearchingWithGameId() {
        val returnedList = target.findGuesses(1, null)
        with(returnedList[0]) {
            assertEquals(gameId, expectedGuess.gameId)
            assertEquals(userId, expectedGuess.userId)
            assertEquals(guessTime, expectedGuess.guessTime)
        }
    }

    @DisplayName("findGuesses() with guessId param will return searched guess")
    @Test
    fun returnListOfResponseWhenSearchingWithGuessId() {
        val returnedList = target.findGuesses(null, 1)
        with(returnedList[0]) {
            assertEquals(gameId, expectedGuess.gameId)
            assertEquals(userId, expectedGuess.userId)
            assertEquals(guessTime, expectedGuess.guessTime)
        }
    }

    private fun getGuessStub() =
        Guess(guessId = 1, gameId = 1, userId = "Z", guessTime = ZonedDateTime.now().withHour(14).withMinute(38))
}
