package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondEphemeral
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import uk.co.mutuallyassureddistraction.paketliga.matching.GameUpsertService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GameTimeParserService

class UpdateGameExtension(
    private val gameUpsertService: GameUpsertService,
    private val gameTimeParserService: GameTimeParserService,
    private val serverId: Snowflake,
) : Extension() {
    override val name = "updateGameExtension"

    override suspend fun setup() {
        publicSlashCommand(::UpdateGameArgs) {
            name = "pklupdate"
            description = "Edit the details of an active game"

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val startWindow = arguments.startwindow
                val closeWindow = arguments.closewindow
                val guessesClose = arguments.guessesclose

                if (startWindow == null && closeWindow == null && guessesClose == null) {
                    respondEphemeral { content = "<:thonk:344120216227414018> You didn't change anything" }
                } else {
                    val updateWindow = gameTimeParserService.parseGameUpdateTime(startWindow, closeWindow, guessesClose)
                    val (responseString, userIds) =
                        gameUpsertService.updateGame(
                            gameId,
                            user.asUser().id.value.toString(),
                            member?.asMember(),
                            updateWindow,
                            user.asUser().username
                        )

                    respond { content = responseString[0] }

                    if (userIds.isNotEmpty()) {
                        val kord = this@UpdateGameExtension.kord
                        var mentionContent =
                            "Attention players, you may wish to update your guesses for game #$gameId\n"
                        userIds.forEach {
                            val memberBehavior = MemberBehavior(serverId, Snowflake(it), kord)
                            mentionContent += " " + memberBehavior.asMember().mention + "\n"
                        }

                        respond { content = mentionContent }
                    }
                }
            }
        }
    }

    inner class UpdateGameArgs : Arguments() {
        val gameid by int {
            name = "gameid"
            description = "The game ID announced by Dr Pakidge when the game was created"
        }

        val startwindow by optionalString {
            name = "delivery-from"
            description = "New start of delivery window"
        }

        val closewindow by optionalString {
            name = "delivery-by"
            description = "New end of delivery window"
        }

        val guessesclose by optionalString {
            name = "guesses-until"
            description = "New deadline for guesses"
        }
    }
}
