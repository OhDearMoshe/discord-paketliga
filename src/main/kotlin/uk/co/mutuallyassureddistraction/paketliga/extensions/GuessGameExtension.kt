package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.int
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.matching.GuessUpsertService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTimeParserService

class GuessGameExtension(
    private val guessUpsertService: GuessUpsertService,
    private val guessTimeParserService: GuessTimeParserService,
    private val serverId: Snowflake,
) : Extension() {
    override val name = "guessExtension"

    override suspend fun setup() {
        publicSlashCommand(::GuessGameArgs) {
            name = "pklguess".toKey()
            description = "Submit a delivery time guess".toKey()

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val userId = user.asUser().id.value.toString()
                val mention = user.asUser().mention
                val guessTime = guessTimeParserService.parseToGuessTime(arguments.guesstime)
                val guessGameResponse = guessUpsertService.guessGame(gameId, guessTime, userId, mention)

                respond { content = guessGameResponse }
            }
        }
    }

    inner class GuessGameArgs : Arguments() {
        val gameid by int {
            name = "gameid".toKey()
            description = "Include the game ID announced by Dr Pakidge when the game was created".toKey()
        }
        val guesstime by string {
            name = "guesstime".toKey()
            description = "Go on, give us your guess".toKey()
        }
    }
}
