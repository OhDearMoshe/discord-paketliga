package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake

class ContributeExtension (private val serverId: Snowflake) : Extension() {
    override val name = "contributeExtension"
    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcontrbiute"
            description = "Explain how to raise bugs or contribute"
            guild(serverId)

            action {
                respond {
                    content = """
                        :postal_horn: Discord guessing game. How to contribute :postal_horn:
                        
                        * Source code is hosted at: <https://github.com/OhDearMoshe/discord-paketliga>
                        * Found a bug? Please raise an issues or a PR
                        * Have a feature request? Please raise an issues or a PR
                        * Want to suggest an improvement? Please raise an issue or PR
                        * Snark? Direct to OhDearMoshe then go contemplate your life choices
                    """.trimIndent()
                }
            }
        }
    }
}