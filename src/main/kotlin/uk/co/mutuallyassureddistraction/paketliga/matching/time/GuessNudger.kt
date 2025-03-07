package uk.co.mutuallyassureddistraction.paketliga.matching.time

import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Game

class GuessNudger {
    // Quite often peoples guesses will fail because they default to just the time aka 12:45
    // but the game is actually tomorrow and the library defaults to today
    // So we are now making the assumption that a guess will always be for the
    // game day if the game starts and ends of different days and so we nudge it there
    fun nudgeGuessToDeliveryDay(game: Game?, guessTime: GuessTime): GuessTime {
        return when {
            game == null -> guessTime
            gameStartsAndEndsOnDifferentDays(game) -> guessTime
            else -> nudgeTime(game, guessTime)
        }
    }

    private fun gameStartsAndEndsOnDifferentDays(game: Game): Boolean {
        val startTime = game.windowStart
        val endTime = game.windowClose
        return startTime.dayOfYear != endTime.dayOfYear
    }

    private fun nudgeTime(game: Game, guessTime: GuessTime): GuessTime {
        return GuessTime(guessTime.guessTime.withDayOfYear(game.windowStart.dayOfYear))
    }
}
