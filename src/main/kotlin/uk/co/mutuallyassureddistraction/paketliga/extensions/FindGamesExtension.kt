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

class FindGamesExtension(private val gameFinderService: GameFinderService, private val serverId: Snowflake) : Extension() {
    override val name = "findGamesExtension"

    override suspend fun setup() {
        publicSlashCommand(::FindGamesArgs) {
            name = "pklfindgames"
            description = "Get a list of active games"

            guild(serverId)

            action {
                val gameId = arguments.gameid
                val gameName = arguments.gamename
                val userId = arguments.gamecreator?.asUser()?.id?.value?.toString()

                val responseList: List<FindGamesResponse> = gameFinderService.findGames(userId, gameName, gameId)

                val kord = this@FindGamesExtension.kord

                if (responseList.isEmpty()) {
                    respond {
                        content = "No games found"
                    }
                } else {
                    val paginator = respondingPaginator {
                        responseList.chunked(5).map { response ->
                            val pageFields = ArrayList<EmbedBuilder.Field>()
                            response.forEach {
                                val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)

                                val field = EmbedBuilder.Field()
                                field.name =
                                    "ID #" + it.gameId.toString() + ": Game by " + memberBehavior.asMember().displayName +
                                            " - " + it.gameName
                                field.value = "Arriving between " + it.windowStart + " and " + it.windowClose +
                                        ".\n Guesses accepted until " + it.guessesClose
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
            name = "gamecreator"
            description = "Filter by creator name"
        }

        val gameid by optionalInt {
            name = "gameid"
            description = "Filter by Game ID"
        }

        val gamename by optionalString {
            name = "description"
            description = "Filter by description"
        }
    }
}