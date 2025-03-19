package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.*

class StatsExtension(
    private val serverId: Snowflake
) : Extension() {

    override val name = "statsExtension"


    override suspend fun setup() {
        publicSlashCommand {
            name = "pklstats".toKey()
            description = "View stats for PKL".toKey()

            guild(serverId)

            action {
                respond { content = "Response content goes here"}
            }
        }
    }
}