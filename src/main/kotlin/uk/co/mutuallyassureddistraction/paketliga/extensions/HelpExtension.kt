package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake

class HelpExtension(private val serverId: Snowflake) : Extension() {
    override val name = "helpExtension"
    override suspend fun setup() {
        publicSlashCommand {
            name = "pklhelp"
            description = "Explain the rules and general info"
            guild(serverId)

            action {
                respond {
                    content = """
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

                    """.trimIndent()
                }
            }
        }
    }
}