package uk.co.mutuallyassureddistraction.paketliga.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalInt
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.types.respondEphemeral
import com.kotlindiscord.kord.extensions.types.respondingPaginator
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import uk.co.mutuallyassureddistraction.paketliga.matching.GuessFinderService

class FindGuessExtension(private val guessFinderService: GuessFinderService, private val serverId: Snowflake) : Extension() {
    override val name = "findGuessExtension"
    override suspend fun setup() {
        publicSlashCommand(::FindGuessArgs) {
            name = "pklfindguess"
            description = "Search for guesses"

            guild(serverId)
            action {
                val gameId = arguments.gameid
                val guessId = arguments.guessid

                if(gameId == null && guessId == null) {
                    respondEphemeral {
                        content = "You didn't enter any search terms"
                    }
                } else {
                    val responseList: List<GuessFinderService.FindGuessesResponse> = guessFinderService.findGuesses(gameId, guessId)

                    val kord = this@FindGuessExtension.kord

                    if (responseList.isEmpty()) {
                        respond {
                            content = "Nothing found for those details"
                        }
                    } else {
                        val paginator = respondingPaginator {
                            responseList.chunked(5).map { response ->
                                val guessFields = ArrayList<EmbedBuilder.Field>()
                                response.forEach {
                                    val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)

                                    val field = EmbedBuilder.Field()
                                    field.name = "Guess #${guessId.toString()}: ${it.guessTime} by ${memberBehavior.asMember().displayName}"
                                    guessFields.add(field)
                                }

                                page {
                                    title = if(arguments.gameid != null) { "Active guesses for game ID #$gameId: " }
                                            else { "PKL guess for guess ID #" + arguments.guessid + ":" }
                                    fields = guessFields
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
    }

    inner class FindGuessArgs : Arguments() {
        val guessid by optionalInt {
            name = "guessid"
            description = "The guess ID provided by Dr Pakidge when you submitted your guess"
        }

        val gameid by optionalInt {
            name = "gameid"
            description = "The game ID announced by Dr Pakidge when the game was created "
        }
    }
}