package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalInt
import dev.kordex.core.commands.converters.impl.optionalString
import dev.kordex.core.commands.converters.impl.optionalUser
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID
import uk.co.mutuallyassureddistraction.paketliga.findGuessGameName
import uk.co.mutuallyassureddistraction.paketliga.findGuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.FindGamesResponse
import uk.co.mutuallyassureddistraction.paketliga.matching.GameFinderService
import uk.co.mutuallyassureddistraction.paketliga.matching.VoidGameService

class FindGamesExtension(
    private val gameFinderService: GameFinderService,
    private val voidGameService: VoidGameService,
    private val serverId: Snowflake,
) : Extension() {
    override val name = "findGamesExtension"

    override suspend fun setup() {
        publicSlashCommand(::FindGamesArgs) {
            name = "pklfindgames".toKey()
            description = "Get a list of active games".toKey()

            guild(serverId)
            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }

                // I want to move this properly to a scheduler but as an interim call
                // here to stop stale games clogging up the find games
                voidGameService.cullExpiredGames()

                val gameId = arguments.gameid
                val gameName = arguments.gamename
                val userId = arguments.gamecreator?.asUser()?.id?.value?.toString()

                val responseList: List<FindGamesResponse> = gameFinderService.findGames(userId, gameName, gameId)

                val kord = this@FindGamesExtension.kord

                if (responseList.isEmpty()) {
                    respond { content = "No games found" }
                } else {

                    val paginator = respondingPaginator {
                        responseList.chunked(5).map { response ->
                            val pageFields = ArrayList<EmbedBuilder.Field>()
                            response.forEach {
                                val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)

                                val field = EmbedBuilder.Field()
                                field.name = findGuessGameName(it, memberBehavior.asMember().effectiveName)
                                field.value = findGuessWindow(it)
                                pageFields.add(field)
                            }

                            page {
                                title = "List of active games: "
                                fields = pageFields
                            }
                        }

                        // This will make the pagination function (next prev etc) to disappear after timeout time
                        timeoutSeconds = 15L
                    }

                    paginator.send()
                }
            }
        }
    }

    inner class FindGamesArgs : Arguments() {
        val gamecreator by optionalUser {
            name = "gamecreator".toKey()
            description = "Filter by creator name".toKey()
        }

        val gameid by optionalInt {
            name = "gameid".toKey()
            description = "Filter by Game ID".toKey()
        }

        val gamename by optionalString {
            name = "description".toKey()
            description = "Filter by description".toKey()
        }
    }
}
