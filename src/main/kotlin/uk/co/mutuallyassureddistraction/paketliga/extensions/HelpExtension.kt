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
                        Discord guessing game 
                        
                        Rules: 
                        
                        1) The addressee must specify the timeframe in which they expect the package to arrive. AKA start: Today 2pm end Today: 3pm 
                        2) Players must guess an exact time of arrival e.g 11:27 or 14:44
                        3) Guesses are accepted up to specified deadline. If no deadline is specified it will default to an hour after game creation, or five minutes before game start if there is not an hour between creation and delivery window
                        4) Guesses can be amended up until the guess deadline
                        5) In the event of a successful delivery. Closest time wins. The winner is awarded with one point
                        6) An additional BONUS point is awarded on guessing exact time of delivery
                        7) In the event of a DRAW e.g two players are an equal number of minutes from time of delivery. Points are split
                        8) If the addressee parcel arrives outside of the delivery window. Game is null and void. NIL Points awarded
                        9) The addressee may update the delivery window if it is delayed
                        10) Deliveroo, Uber Eats, Just Eat etc Deliveries are not allowed. Grocery deliveries (Sainsbury's, Ocado) are though
                        11) Sniping is permitted
                        12) You cannot submit a time already submitted
                        13) Any talk of collusion will be swiftly dealt with. Violently.
                        
                        Commands:
                        * /paketliga -> Create game
                        * /findgames -> Find games
                        * /guessgame -> Create or update guess
                        * /findguess -> Look for guesses
                        * /updategame -> Update a game
                        * /endgame -> Finish a game
                        * /leaderboard -> View leaderboards
                        * /pklhelp -> Look at this message
                        
                        Credits:
                        * To Shreddz, whom this bot wishes it could be even half the postmaster general he was
                        * To Z, who did all the work
                        * To Mike, who bitched this bot into existence
                    """.trimIndent()
                }
            }
        }
    }
}