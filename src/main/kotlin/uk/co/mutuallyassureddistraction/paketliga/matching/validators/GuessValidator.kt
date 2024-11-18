package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class GuessValidator {

    fun validateGuess(game: Game?, gameId: Int, userId: String, guessTime: GuessTime): String? {
        if (game == null) {
            return "Guessing failed, there is no active game with game ID #$gameId"
        }

        if (game.userId == userId) {
            return "Mr Pump forbids you from guessing in your own game."
        }

        if (ZonedDateTime.now() >= game.guessesClose) {
            return "*\\*womp-womp*\\* Too late, the guessing window has closed for game ID #$gameId"
        }

        if (guessTime.guessTime < game.windowStart || guessTime.guessTime > game.windowClose) {
            return "Guesses must be between ${toUserFriendlyString(game.windowStart)} and ${toUserFriendlyString(game.windowClose)}"
        }

        return null
    }
}
