package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.int
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
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
            name = "pklupdate".toKey()
            description = "Edit the details of an active game".toKey()

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val startWindow = arguments.startwindow
                val closeWindow = arguments.closewindow
                val guessesClose = arguments.guessesclose

                if (startWindow == null && closeWindow == null && guessesClose == null) {
                    respond {
                        ephemeral
                        content = "<:thonk:344120216227414018> You didn't change anything"
                    }
                } else {
                    val updateWindow = gameTimeParserService.parseGameUpdateTime(startWindow, closeWindow, guessesClose)
                    val (responseString, userIds) =
                        gameUpsertService.updateGame(
                            gameId,
                            user.asUser().id.value.toString(),
                            member?.asMember(),
                            updateWindow,
                            arguments.carrier,
                            user.asUser().username,
                        )

                    respond { content = responseString[0] }

                    if (userIds.isNotEmpty()) {
                        val kord = this@UpdateGameExtension.kord
                        var mentionContent =
                            "Attention players, you may wish to update your guesses for game #$gameId\n"
                        userIds.forEach {
                            val memberBehavior = MemberBehavior(serverId, Snowflake(it), kord)
                            mentionContent += "${memberBehavior.asMember().mention} \n"
                        }

                        respond { content = mentionContent }
                    }
                }
            }
        }
    }

    inner class UpdateGameArgs : Arguments() {
        val gameid by int {
            name = "gameid".toKey()
            description = "The game ID announced by Dr Pakidge when the game was created".toKey()
        }

        val startwindow by optionalString {
            name = "delivery-from".toKey()
            description = "New start of delivery window".toKey()
        }

        val closewindow by optionalString {
            name = "delivery-by".toKey()
            description = "New end of delivery window".toKey()
        }

        val guessesclose by optionalString {
            name = "guesses-until".toKey()
            description = "New deadline for guesses".toKey()
        }

        val carrier by optionalString {
            name = "description".toKey()
            description = "New carrier for your parcel".toKey()
        }
    }
}
