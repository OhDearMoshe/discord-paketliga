package uk.co.mutuallyassureddistraction.paketliga.matching.results

import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.dao.PointDao
import uk.co.mutuallyassureddistraction.paketliga.dao.WinDao
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Guess
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Point
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.Win

class PointUpdaterService(private val pointDao: PointDao, private val winDao: WinDao) {
    fun applyPoints(gameResult: GameResult) {
        gameResult.winners.forEach {
            if (gameResult.awardBonusPoint) {
                applyBonusWin(it)
            } else if (gameResult.wasDraw) {
                applyDraw(it)
            } else {
                applyWin(it)
            }
        }

        gameResult.losers.forEach { applyLoss(it) }
    }

    private fun applyWin(guess: Guess) {
        winDao.addWinningGuess(Win(gameId = guess.gameId, guessId = guess.guessId!!, date = ZonedDateTime.now()))
        pointDao.addWin(Point(userId = guess.userId, played = 1, won = 1, totalPoint = 1F))
    }

    private fun applyLoss(guess: Guess) {
        pointDao.addLost(Point(pointId = null, userId = guess.userId, played = 1, lost = 1, totalPoint = 0F))
    }

    private fun applyDraw(guess: Guess) {
        winDao.addWinningGuess(Win(gameId = guess.gameId, guessId = guess.guessId!!, date = ZonedDateTime.now()))
        pointDao.addDraw(Point(userId = guess.userId, played = 1, drawn = 1, totalPoint = 0.5F))
    }

    private fun applyBonusWin(guess: Guess) {
        winDao.addWinningGuess(Win(gameId = guess.gameId, guessId = guess.guessId!!, date = ZonedDateTime.now()))
        pointDao.addBonusWin(Point(userId = guess.userId, played = 1, won = 1, bonus = 1, totalPoint = 2F))
    }
}
