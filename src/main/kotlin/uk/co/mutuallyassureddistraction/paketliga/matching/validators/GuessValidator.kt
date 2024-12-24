package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class GuessValidator {
    fun validateGuess(game: Game?, gameId: Int, userId: String, guessTime: GuessTime): String? =
        when {
            game == null -> "Guessing failed, there is no active game with game ID #$gameId"

            game.userId == userId -> "Mr Pump forbids you from guessing in your own game."

            ZonedDateTime.now() >= game.guessesClose ->
                "*\\*womp-womp*\\* Too late, the guessing window has closed for game ID #$gameId"

            guessTime.guessTime < game.windowStart || guessTime.guessTime > game.windowClose ->
                "Guesses must be between ${game.windowStart.toUserFriendlyString()} and ${game.windowClose.toUserFriendlyString()}"

            else -> null
        }
}
