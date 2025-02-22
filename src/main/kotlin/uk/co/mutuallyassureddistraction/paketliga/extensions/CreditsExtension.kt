package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.CreditsExtensionMessage
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID

class CreditsExtension(private val serverId: Snowflake) : Extension() {
    override val name = "creditsextension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcredits".toKey()
            description = "Roll credits".toKey()
            guild(serverId)

            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }
                respond { content = CreditsExtensionMessage }
            }
        }
    }
}
