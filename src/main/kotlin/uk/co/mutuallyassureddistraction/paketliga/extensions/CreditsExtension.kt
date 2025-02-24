package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.CreditsExtensionMessage

class CreditsExtension(private val serverId: Snowflake) : Extension() {
    override val name = "creditsextension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcredits".toKey()
            description = "Roll credits".toKey()
            guild(serverId)

            action { respond { content = CreditsExtensionMessage } }
        }
    }
}
