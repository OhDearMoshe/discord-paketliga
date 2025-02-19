package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import uk.co.mutuallyassureddistraction.paketliga.ContributeExtensionMessage

class ContributeExtension(private val serverId: Snowflake) : Extension() {
    override val name = "contributeExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcontribute"
            description = "Explain how to raise bugs or contribute"
            guild(serverId)

            action { respond { content = ContributeExtensionMessage } }
        }
    }
}
