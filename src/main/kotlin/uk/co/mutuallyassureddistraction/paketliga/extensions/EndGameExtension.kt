package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import java.util.logging.Logger
import uk.co.mutuallyassureddistraction.paketliga.matching.GameEndService
import uk.co.mutuallyassureddistraction.paketliga.matching.LeaderboardService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.DeliveryTimeParser
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class EndGameExtension(
    private val gameEndService: GameEndService,
    private val leaderboardService: LeaderboardService,
    private val deliveryTimeParser: DeliveryTimeParser,
    private val topOfLeaderBoardRole: Snowflake,
    private val serverId: Snowflake,
) : Extension() {
    override val name = "endGameExtension"
    private val LOGGER = Logger.getLogger(EndGameExtension::class.java.name)

    override suspend fun setup() {
        publicSlashCommand(::EndGameArgs) {
            name = "pklend"
            description = "Finalise an open game"

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val deliveryTime = deliveryTimeParser.parse(arguments.deliverytime)

                val pastWinner = leaderboardService.getLeaderboard(null, 1)
                val (responseString, result) = gameEndService.endGame(gameId, deliveryTime)
                val currentWinner = leaderboardService.getLeaderboard(null, 1)

                try {
                    val kord = this@EndGameExtension.kord

                    if (responseString != null) {
                        respond { content = responseString }
                    } else if (result != null) {
                        respond { content = "Game #$gameId ended with delivery time: ${deliveryTime.toHumanTime()}" }
                        var mentionContent = "We have a winner:"
                        if (result.winners.isEmpty()) {
                            mentionContent = "No one guessed the time, so no winners at this game."
                        } else if (result.winners.size > 1) {
                            mentionContent = "We have multiple winners:"
                        }

                        val guessesIterator = result.winners.iterator()
                        while (guessesIterator.hasNext()) {
                            val currentGuess = guessesIterator.next()
                            val memberBehavior = MemberBehavior(serverId, Snowflake(currentGuess.userId), kord)
                            mentionContent += " " + memberBehavior.asMember().mention
                            mentionContent += " with guess time at " + currentGuess.guessTime.toUserFriendlyString()
                            if (guessesIterator.hasNext()) mentionContent += ", "
                        }

                        respond { content = mentionContent }

                        if (currentWinner.isNotEmpty()) {
                            val roleContent =
                                resolveRole(
                                    currentWinner.first().userId,
                                    currentWinner.first().totalPoint,
                                    pastWinner.firstOrNull()?.userId,
                                    topOfLeaderBoardRole,
                                    kord,
                                )

                            respond { content = roleContent }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    LOGGER.info(e.message)
                }
            }
        }
    }

    private suspend fun resolveRole(
        currentWinner: String,
        currentWinnerPoint: Float,
        pastWinner: String?,
        topOfLeaderBoardRole: Snowflake,
        kord: Kord,
    ): String {
        val currentWinnerMember = MemberBehavior(serverId, Snowflake(currentWinner), kord)
        currentWinnerMember.asMember().addRole(topOfLeaderBoardRole)

        if (pastWinner == null || currentWinner == pastWinner) {
            return currentWinnerMember.asMember().mention +
                " is on top the leaderboard with " +
                currentWinnerPoint +
                " points"
        }

        // If there is a past rank 1 / change of top of leaderboard:

        val pastWinnerMember = MemberBehavior(serverId, Snowflake(pastWinner), kord)
        pastWinnerMember.asMember().removeRole(topOfLeaderBoardRole)

        return currentWinnerMember.asMember().mention +
            " is now on top the leaderboard with " +
            currentWinnerPoint +
            " points, sorry " +
            pastWinnerMember.asMember().mention +
            " better luck next time."
    }

    inner class EndGameArgs : Arguments() {
        val gameid by int {
            name = "gameid"
            description = "The game ID announced by Dr Pakidge when the game was created"
        }

        val deliverytime by string {
            name = "deliverytime"
            description = "What time the package arrived"
        }
    }
}
