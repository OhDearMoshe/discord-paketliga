package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake

class CreditsExtension (private val serverId: Snowflake) : Extension() {
    override val name = "creditsextension"
    override suspend fun setup() {
        publicSlashCommand {
            name = "pklcredits"
            description = "Roll credits"
            guild(serverId)

            action {
                respond {
                    content = """
                        :postal_horn: Discord guessing game. Credits :postal_horn:
                        
                        * For Shreddz, who was a better postmaster general than this bot ever could be
                        * For Z, who did most of the work really
                        * For Mike, who bitched this bot into existence
                    
                    """.trimIndent()
                }
            }
        }
    }
}