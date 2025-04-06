package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.matching.StatsService

class StatsExtension(private val statsService: StatsService, private val serverId: Snowflake) : Extension() {
    override val name = "statsExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklstats".toKey()
            description = "View PaketLiga stats".toKey()

            guild(serverId)

            action {
                val kord = this@StatsExtension.kord

                respond {
                    val mostGamesByUser = statsService.findCreatedGamesByUsers().first()
                    val mostPopularCarriers = statsService.findMostPopularCarriers().first()
                    val mostVoidedGamesByUser = statsService.findUsersWithMostVoidedGames().first()

                    val mostVoidedCarriers = statsService.findCarriersWithMostVoidedGames()
                    val mostVoidedCarrier =
                        if (mostVoidedCarriers.first().carrier == "N/A" && mostVoidedCarriers.size > 1) {
                            mostVoidedCarriers[1]
                        } else {
                            mostVoidedCarriers.first()
                        }

                    content =
                        """
                        :postal_horn: PaketLiga General Stats :postal_horn:
                        ```
                        User with most games: ${getMemberNameFromUserId(mostGamesByUser.userId, kord)}
                        User with most voided games: ${getMemberNameFromUserId(mostVoidedGamesByUser.userId, kord)}
                        Most popular carrier: ${mostPopularCarriers.carrier}
                        Most voided carrier: ${mostVoidedCarrier.carrier}
                        ```
                        """.trimIndent()
                }
            }
        }
    }

    private suspend fun getMemberNameFromUserId(userId: String, kord: Kord): String {
        return MemberBehavior(serverId, Snowflake(userId), kord).asMember().effectiveName
    }
}
