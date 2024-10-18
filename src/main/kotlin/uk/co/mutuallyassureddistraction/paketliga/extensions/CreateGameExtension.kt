package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalString
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import uk.co.mutuallyassureddistraction.paketliga.matching.GameTimeParserService
import uk.co.mutuallyassureddistraction.paketliga.matching.GameUpsertService
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GameValidator

class CreateGameExtension(private val gameUpsertService: GameUpsertService,
                          private val gameTimeParserService: GameTimeParserService,
                          private val serverId: Snowflake) : Extension() {
    override val name = "createGameExtension"

    override suspend fun setup() {
        publicSlashCommand(::PaketGameArgs) {  // Public slash commands have public responses
            name = "paketliga"
            description = "Ask the bot to create a game of PKL"

            // Use guild commands for testing, global ones take up to an hour to update
            guild(serverId)

            action {
                val guessWindow = gameTimeParserService.parseGameTime(arguments.startwindow, arguments.closewindow, arguments.guessesclose)
                val responseMessage = gameUpsertService.createGame(
                    arguments.gamename, guessWindow,
                    user.asUser().id.value.toString(), member?.asMember(), user.asUser().username
                )
                respond {
                    content = responseMessage
                }

                //TODO put the logic in try/catch and add logging?
            }
        }
    }

    /**
     * Arguments for the game, basically for this extension.
     * Planned arguments are similar to Game Entity:
     * from user input: startWindow, closeWindow, guessesClose, gameName (optional)
     * from impl: userId
     * We're not using camelCase because currently it doesn't work (counted as coalesced string)
     */
    inner class PaketGameArgs : Arguments() {
        val startwindow by string {
            name = "startwindow"
            description = "Start window time inputted by user"
        }

        val closewindow by string {
            name = "closewindow"
            description = "Close window time inputted by user"
        }

        val guessesclose by optionalString {
            name = "guessesclose"
            description = "Close window time inputted by user"
        }

        val gamename by optionalString {
            name = "gamename"
            description = "Game name inputted by user"
        }
    }
}
