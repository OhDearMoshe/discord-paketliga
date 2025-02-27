package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey

class HelpExtension(private val serverId: Snowflake) : Extension() {
    override val name = "helpExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklhelp".toKey()
            description = "Explain the rules and general info".toKey()
            guild(serverId)

            action {
                respond {
                    content =
                        """
                        :postal_horn: Discord guessing game :postal_horn:
                        
                        Commands:
                        * /pkl -> Create game
                        * /pklfindgames -> Find games
                        * /pklguess -> Create or update guess
                        * /pklfindguess -> Look for guesses
                        * /pklupdate -> Update a game
                        * /pklend -> Finish a game
                        * /pklrank -> View leaderboards
                        * /pklvoidgame -> Void a game
                        * /pklhelp -> Look at this message
                        * /pklrules -> View the rules
                        * /pklcontribute -> Raise bugs. See how to contribute
                        * /pklcredits -> View credits
                    
                        Guessing times: time guessing uses a library that is designed parse naturally spoken time
                        (things like tomorrow 11:11AM) although can sometimes be a little funny about what is parses. 
                        pkl-bot will nudge times to what we think its best where it makes sense (when guessing
                        and ending a game we will nudge the day to be day of delivery)
                        
                        That said here are some tips on how to get what you want out of it:
                        * Time without a qualifier will guess the next occurrence of that time (11:11 will default to the
                          next time it is 11:11)
                        * It does not always do too well with days of the week when setting up your time tomorrow works 
                          better (aka delivery-from: tomorrow 11AM rather than Thursday 11AM)
                        * Dates also dont always work well without a year (03/03/2025 works great, 03/03 will confuse
                          it sometimes)
                    """
                            .trimIndent()
                }
            }
        }
    }
}
