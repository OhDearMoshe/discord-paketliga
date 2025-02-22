package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.chatCommand
import dev.kordex.core.i18n.toKey
import dev.kordex.core.utils.respond

class SlapExtension : Extension() {
    override val name = "slapExtension"

    override suspend fun setup() {
        chatCommand {
            name = "slap".toKey()
            description = "Get slapped!".toKey()

            action { message.respond("*slaps you with a large, smelly trout!*") }
        }
    }
}
