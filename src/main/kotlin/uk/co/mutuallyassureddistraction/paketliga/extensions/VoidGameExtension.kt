package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.int
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID
import uk.co.mutuallyassureddistraction.paketliga.matching.VoidGameService

class VoidGameExtension(private val voidGameService: VoidGameService, private val serverId: Snowflake) : Extension() {
    override val name = "voidGame"

    override suspend fun setup() {
        publicSlashCommand(::VoidGameArguments) {
            name = "pklvoid".toKey()
            description = "Void an active game".toKey()

            guild(serverId)

            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }
                val resultMessage =
                    voidGameService.voidGame(arguments.gameid, arguments.reason, user.asUser().id.value.toString())
                respond { content = resultMessage }
            }
        }
    }

    inner class VoidGameArguments : Arguments() {
        val gameid by int {
            name = "gameid".toKey()
            description = "The game ID announced by Dr Pakidge when the game was created".toKey()
        }
        val reason by optionalString {
            name = "reason".toKey()
            description = "The reason you want to void this game".toKey()
        }
    }
}
