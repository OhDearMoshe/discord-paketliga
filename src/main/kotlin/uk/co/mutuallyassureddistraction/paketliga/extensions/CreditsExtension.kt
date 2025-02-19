package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import uk.co.mutuallyassureddistraction.paketliga.CreditsExtensionMessage

class CreditsExtension(private val serverId: Snowflake) : Extension() {
    override val name = "creditsextension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcredits"
            description = "Roll credits"
            guild(serverId)

            action { respond { content = CreditsExtensionMessage } }
        }
    }
}
