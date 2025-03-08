package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.ReleaseNotes

class ReleaseNotesExtension(private val serverId: Snowflake) : Extension() {
    override val name = "releasenotes"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklreleasenotes".toKey()
            description = "Show release notes for this version".toKey()
            guild(serverId)
            action { respond { content = ReleaseNotes } }
        }
    }
}
