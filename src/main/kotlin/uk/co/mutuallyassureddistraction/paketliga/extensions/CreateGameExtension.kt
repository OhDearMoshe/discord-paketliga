package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.ExtensibleBot
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.string
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import org.koin.core.component.inject
import uk.co.mutuallyassureddistraction.paketliga.matching.GameUpsertService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GameTimeParserService

class CreateGameExtension(
    private val gameUpsertService: GameUpsertService,
    private val gameTimeParserService: GameTimeParserService,
    private val serverId: Snowflake,
) : Extension() {
    override val name = "createGameExtension"
    override val bot: ExtensibleBot by inject()

    override suspend fun setup() {
        publicSlashCommand(::PaketGameArgs) { // Public slash commands have public responses
            name = "pkl".toKey()
            description = "Start a game".toKey()

            // Use guild commands for testing, global ones take up to an hour to update
            guild(serverId)

            action {
                val guessWindow =
                    gameTimeParserService.parseGameTime(
                        arguments.startwindow,
                        arguments.closewindow,
                        arguments.guessesclose,
                    )
                val responseMessage =
                    gameUpsertService.createGame(
                        arguments.gamename,
                        guessWindow,
                        arguments.carrier,
                        user.asUser().id.value.toString(),
                        member?.asMember(),
                        user.asUser().username,
                        bot,
                    )

                respond { content = responseMessage }
            }
        }
    }

    /**
     * Arguments for the game, basically for this extension. Planned arguments are similar to Game Entity: from user
     * input: startWindow, closeWindow, guessesClose, gameName (optional) from impl: userId We're not using camelCase
     * because currently it doesn't work (counted as coalesced string)
     */
    inner class PaketGameArgs : Arguments() {
        val startwindow by string {
            name = "delivery-from".toKey()
            description = "Start of delivery window".toKey()
        }

        val closewindow by string {
            name = "delivery-by".toKey()
            description = "End of delivery window".toKey()
        }

        val guessesclose by optionalString {
            name = "guesses-until".toKey()
            description = "(Optional) Deadline for guesses".toKey()
        }

        val gamename by optionalString {
            name = "description".toKey()
            description = "(Optional) A short description of the game".toKey()
        }

        val carrier by optionalString {
            name = "carrier".toKey()
            description = "(Optional) Who is delivering your parcel?".toKey()
        }
    }
}
