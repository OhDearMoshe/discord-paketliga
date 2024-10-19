package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import uk.co.mutuallyassureddistraction.paketliga.matching.GameEndService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.DeliveryTimeParser
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString
import java.util.logging.Logger

class EndGameExtension(private val gameEndService: GameEndService,
                        private val deliveryTimeParser: DeliveryTimeParser,
                       private val serverId: Snowflake) : Extension() {
    override val name = "endGameExtension"
    private val LOGGER = Logger.getLogger(EndGameExtension::class.java.name)

    override suspend fun setup() {
        publicSlashCommand(::EndGameArgs) {
            name = "endgame"
            description = "Ask the bot to end a game of PKL"

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val deliveryTime = deliveryTimeParser.parse(arguments.deliverytime)

                val (responseString, result) = gameEndService.endGame(gameId, deliveryTime)

                try {
                    if (responseString != null ) {
                        respond {
                            content = responseString
                        }
                    } else if(result != null) {
                        respond {
                            content = "Game #$gameId ended with delivery time: ${deliveryTime.toHumanTime()}"
                        }
                        var mentionContent = "We have a winner:"
                        if (result.winners.isEmpty()) {
                            mentionContent = "No one guessed the time, so no winners at this game."
                        } else if (result.winners.size > 1) {
                            mentionContent = "We have multiple winners:"
                        }

                        val kord = this@EndGameExtension.kord
                        val guessesIterator = result.winners.iterator()
                        while (guessesIterator.hasNext()) {
                            val currentGuess = guessesIterator.next()
                            val memberBehavior = MemberBehavior(serverId, Snowflake(currentGuess.userId), kord)
                            mentionContent += " " + memberBehavior.asMember().mention
                            mentionContent += " with guess time at " + toUserFriendlyString(currentGuess.guessTime)
                            if (guessesIterator.hasNext()) mentionContent += ", "
                        }

                        respond {
                            content = mentionContent
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LOGGER.info(e.message)
                }
            }
        }
    }

    inner class EndGameArgs : Arguments() {
        val gameid by int {
            name = "gameid"
            description = "Game id inputted by user"
        }

        val deliverytime by string {
            name = "deliverytime"
            description = "Actual delivery time inputted by user"
        }
    }
}
