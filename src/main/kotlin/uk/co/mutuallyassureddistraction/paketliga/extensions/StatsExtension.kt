package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.matching.StatsService

class StatsExtension(private val statsService: StatsService, private val serverId: Snowflake) : Extension() {
    override val name = "statsExtension"

    override suspend fun setup() {
        publicSlashCommand(::StatsArgs) {
            name = "pklstats".toKey()
            description = "View PaketLiga stats".toKey()

            guild(serverId)

            action {
                val statsType = arguments.statsType
                if (statsType.isNullOrEmpty() || statsType == "help") {
                    respond {
                        content =
                            """
                            Choose your stats type:
                            * `gamescreated` -> Show ranks for most game created. Capitalism, baby
                        """
                                .trimIndent()
                    }
                } else {
                    val kord = this@StatsExtension.kord
                    when (statsType.lowercase()) {
                        "gamescreated" -> {
                            val gameCreated = statsService.findCreatedGames()

                            val paginator = respondingPaginator {
                                var counter = 1
                                gameCreated.chunked(10).map { response ->
                                    val pageFields = ArrayList<EmbedBuilder.Field>()
                                    response.forEach {
                                        val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)
                                        val field = EmbedBuilder.Field()
                                        field.inline = true
                                        field.name =
                                            "# $counter : ${memberBehavior.asMember().effectiveName}" +
                                                " | ${it.gameCount} games created"
                                        field.value = "Most used carrier : **${it.mostCarrier}**"
                                        pageFields.add(field)
                                        counter++
                                    }

                                    page {
                                        title = "PaketLiga - Number of Games Created: "
                                        fields = pageFields
                                    }
                                }
                                timeoutSeconds = 20L
                            }
                            paginator.send()
                        }
                        else -> {
                            respond {
                                ephemeral
                                content = "No stats of type $statsType found"
                            }
                        }
                    }
                }
            }
        }
    }

    inner class StatsArgs : Arguments() {
        val statsType by optionalString {
            name = "type".toKey()
            description = "Stats type to return".toKey()
        }
    }
}
