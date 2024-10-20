package uk.co.mutuallyassureddistraction.paketliga.matching.validators

import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString
import java.time.ZonedDateTime

class GuessValidator {

    fun validateGuess(game: Game?, gameId: Int,  userId: String, guessTime: GuessTime): String? {
        if(game == null) {
            return "Guessing failed, there is no active game with game ID #$gameId"
        }

//        if(game.userId == userId) {
//            return "Mr Pump forbids you from guessing in your own game."
//        }

        if(ZonedDateTime.now() >= game.guessesClose) {
            val guessesCloseString = toUserFriendlyString(game.guessesClose)
            return "Guessing failed, guessing deadline for game #$gameId has passed, guessing deadline was at $guessesCloseString"
        }

        if(guessTime.guessTime < game.windowStart || guessTime.guessTime > game.windowClose) {
            return "Guesses must be between ${toUserFriendlyString(game.windowStart)} and ${toUserFriendlyString(game.windowClose)}"
        }


        return null
    }
}