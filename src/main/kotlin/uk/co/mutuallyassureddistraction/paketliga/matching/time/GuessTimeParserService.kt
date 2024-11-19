package uk.co.mutuallyassureddistraction.paketliga.matching.time

class GuessTimeParserService(private val timeParser: TimeParser) {
    fun parseToGuessTime(guessTime: String): GuessTime {
        return GuessTime(timeParser.parseDate(guessTime))
    }
}
