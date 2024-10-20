package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalInt
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalString
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalUser
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondingPaginator
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import uk.co.mutuallyassureddistraction.paketliga.matching.FindGamesResponse
import uk.co.mutuallyassureddistraction.paketliga.matching.GameFinderService

class AssignRoleExtension(private val serverId: Snowflake) : Extension() {
    override val name = "assignRoleExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "assignrole"
            description = "Ask the bot to assign role"
            guild(serverId)

            action {
                respond {
                    user.asMember(Snowflake(user.asUser().id.value.toString()))
                        .addRole(Snowflake(1297534327927603230), "Professional time waster")

                    content = "Role assigned"
                }
            }
        }
    }
}