package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import uk.co.mutuallyassureddistraction.paketliga.matching.GuessUpsertService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTimeParserService

class GuessGameExtension(private val guessUpsertService: GuessUpsertService,
                         private val guessTimeParserService: GuessTimeParserService,
                         private val serverId: Snowflake
                         ): Extension() {
    override val name = "guessExtension"

    override suspend fun setup() {
        publicSlashCommand(::GuessGameArgs) {
            name = "pklguess"
            description = "Submit a delivery time guess"

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val userId = user.asUser().id.value.toString()
                val mention = user.asUser().mention
                val guessTime = guessTimeParserService.parseToGuessTime(arguments.guesstime)
                val guessGameResponse = guessUpsertService.guessGame(gameId, guessTime, userId, mention)

                respond {
                    content = guessGameResponse
                }
            }
        }
    }

    inner class GuessGameArgs : Arguments() {
        val gameid by int {
            name = "gameid"
            description = "Include the game ID announced by Dr Pakidge when the game was created"
        }
        val guesstime by string {
            name = "guesstime"
            description = "Go on, give us your guess"
        }
    }
}