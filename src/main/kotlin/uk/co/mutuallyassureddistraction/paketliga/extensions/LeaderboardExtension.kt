package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID
import uk.co.mutuallyassureddistraction.paketliga.matching.LeaderboardService

class LeaderboardExtension(private val leaderboardService: LeaderboardService, private val serverId: Snowflake) :
    Extension() {
    override val name = "leaderboardExtension"

    override suspend fun setup() {
        publicSlashCommand(::LeaderboardArgs) {
            name = "pklrank".toKey()
            description = "View the PaketLiga Leaderboard".toKey()

            guild(serverId)

            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }
                val userId = arguments.userId?.asUser()?.id?.value?.toString()

                val leaderboard = leaderboardService.getLeaderboard(userId, null)

                if (leaderboard.isEmpty()) {
                    respond {
                        ephemeral
                        content = "No data found"
                    }
                } else {
                    val kord = this@LeaderboardExtension.kord

                    val paginator = respondingPaginator {
                        var counter = 1
                        leaderboard.chunked(10).map { response ->
                            val pageFields = ArrayList<EmbedBuilder.Field>()
                            response.forEach {
                                val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)

                                val field = EmbedBuilder.Field()
                                field.name =
                                    "# " +
                                        counter +
                                        " : " +
                                        memberBehavior.asMember().effectiveName +
                                        " | " +
                                        it.totalPoint +
                                        " points"
                                field.value = "Played: " + it.played + " - Won: " + it.won + " - Lost: " + it.lost
                                pageFields.add(field)
                                counter++
                            }

                            page {
                                title = "PaketLiga Leaderboard: "
                                fields = pageFields
                            }
                        }

                        // This will make the pagination function (next prev etc) to disappear after timeout time
                        timeoutSeconds = 20L
                    }

                    paginator.send()
                }
            }
        }
    }

    inner class LeaderboardArgs : Arguments() {
        val userId by optionalUser {
            name = "username".toKey()
            description = "Filter by username".toKey()
        }
    }
}
