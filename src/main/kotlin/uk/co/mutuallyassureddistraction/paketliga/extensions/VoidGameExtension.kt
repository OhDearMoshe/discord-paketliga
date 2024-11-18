package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.int
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import uk.co.mutuallyassureddistraction.paketliga.matching.VoidGameService

class VoidGameExtension(private val voidGameService: VoidGameService, private val serverId: Snowflake) : Extension() {
    override val name = "voidGame"

    override suspend fun setup() {
        publicSlashCommand(::VoidGameArguments) {
            name = "pklvoid"
            description = "Void an active game"

            guild(serverId)

            action {
                val resultMessage = voidGameService.voidGame(arguments.gameid, user.asUser().id.value.toString())
                respond { content = resultMessage }
            }
        }
    }

    inner class VoidGameArguments : Arguments() {
        val gameid by int {
            name = "gameid"
            description = "The game ID announced by Dr Pakidge when the game was created"
        }
    }
}
