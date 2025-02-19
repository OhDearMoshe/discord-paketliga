package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.GuessingInOwnGameError
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.guessOutsideOfGuessWindow
import uk.co.mutuallyassureddistraction.paketliga.guessValidatorGameIsNullError
import uk.co.mutuallyassureddistraction.paketliga.guessingWindowClosedError
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime

class GuessValidator {
    fun validateGuess(game: Game?, gameId: Int, userId: String, guessTime: GuessTime): String? =
        when {
            game == null -> guessValidatorGameIsNullError(gameId)

            game.userId == userId -> GuessingInOwnGameError

            ZonedDateTime.now() >= game.guessesClose -> guessingWindowClosedError(gameId)

            guessTime.guessTime < game.windowStart || guessTime.guessTime > game.windowClose ->
                guessOutsideOfGuessWindow(game.windowStart, game.windowClose)

            else -> null
        }
}
