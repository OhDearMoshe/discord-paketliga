package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.MemberBehavior
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.optionalInt
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.matching.FindGuessesResponse
import uk.co.mutuallyassureddistraction.paketliga.matching.GuessFinderService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

class FindGuessExtension(private val guessFinderService: GuessFinderService, private val serverId: Snowflake) :
    Extension() {
    override val name = "findGuessExtension"

    override suspend fun setup() {
        publicSlashCommand(::FindGuessArgs) {
            name = "pklfindguess".toKey()
            description = "Search for guesses".toKey()

            guild(serverId)
            action {
                val gameId = arguments.gameid
                val guessId = arguments.guessid

                if (gameId == null && guessId == null) {
                    respond {
                        ephemeral
                        content = "You didn't enter any search terms"
                    }
                } else {
                    val responseList: List<FindGuessesResponse> = guessFinderService.findGuesses(gameId, guessId)

                    val kord = this@FindGuessExtension.kord

                    if (responseList.isEmpty()) {
                        respond { content = "Nothing found for those details" }
                    } else {

                        val paginator = respondingPaginator {
                            responseList
                                .sortedBy { it.guessTime }
                                .chunked(5)
                                .map { response ->
                                    val guessFields = ArrayList<EmbedBuilder.Field>()
                                    response.forEach {
                                        val memberBehavior = MemberBehavior(serverId, Snowflake(it.userId), kord)

                                        val field = EmbedBuilder.Field()
                                        field.name =
                                            "Guess #${it.guessId}: ${it.guessTime.toUserFriendlyString()} by ${memberBehavior.asMember().effectiveName}"
                                        guessFields.add(field)
                                    }

                                    page {
                                        title =
                                            if (arguments.gameid != null) {
                                                "Active guesses for game ID #$gameId: "
                                            } else {
                                                "PKL guess for guess ID #" + arguments.guessid + ":"
                                            }
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
            name = "guessid".toKey()
            description = "The guess ID provided by Dr Pakidge when you submitted your guess".toKey()
        }

        val gameid by optionalInt {
            name = "gameid".toKey()
            description = "The game ID announced by Dr Pakidge when the game was created".toKey()
        }
    }
}
