package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID

class HelpExtension(private val serverId: Snowflake) : Extension() {
    override val name = "helpExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklhelp".toKey()
            description = "Explain the rules and general info".toKey()
            guild(serverId)

            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }
                respond {
                    content =
                        """
                        :postal_horn: Discord guessing game :postal_horn:
                        
                        Commands:
                        * /paketliga -> Create game
                        * /findgames -> Find games
                        * /guessgame -> Create or update guess
                        * /findguess -> Look for guesses
                        * /updategame -> Update a game
                        * /endgame -> Finish a game
                        * /leaderboard -> View leaderboards
                        * /voidgame -> Void a game
                        * /pklhelp -> Look at this message
                        * /pklrules -> View the rules
                        * /pklcontribute -> Raise bugs. See how to contribute
                        * /pklcredits -> View credits

                    """
                            .trimIndent()
                }
            }
        }
    }
}
