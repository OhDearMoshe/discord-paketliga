package uk.co.mutuallyassureddistraction.paketliga.matching

import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess

class GuessFinderService(private val guessDao: GuessDao) {
    fun findGuesses(gameId: Int?, guessId: Int?): List<FindGuessesResponse> {
        val guessResponseList = ArrayList<FindGuessesResponse>()
        if (gameId == null && guessId == null) {
            return guessResponseList
        }

        if (gameId != null) {
            val searchedGuesses: List<Guess> = guessDao.findGuessesByGameId(gameId)
            for (searchedGuess in searchedGuesses) {
                guessResponseList.add(buildResponse(searchedGuess))
            }
        } else {
            val searchedGuess: Guess = guessDao.findGuessByGuessId(guessId!!)
            guessResponseList.add(buildResponse(searchedGuess))
        }

        return guessResponseList
    }

    private fun buildResponse(guess: Guess): FindGuessesResponse =
        FindGuessesResponse(guess.guessId!!, guess.gameId, guess.userId, guess.guessTime)
}
