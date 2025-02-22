package uk.co.mutuallyassureddistraction.paketliga.extensions

import dev.kord.common.entity.Snowflake
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import dev.kordex.core.i18n.toKey
import uk.co.mutuallyassureddistraction.paketliga.DELIVERY_CHANNEL_ID

class RulesExtension(private val serverId: Snowflake) : Extension() {
    override val name = "rulesExtension"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pklrules".toKey()
            description = "Explain the rules".toKey()
            guild(serverId)

            action {
                if (this.channel.id != DELIVERY_CHANNEL_ID) {
                    return@action
                }
                respond {
                    content =
                        """
                        :postal_horn: Discord guessing game :postal_horn:
                        Rules:
                        1. The addressee must specify the timeframe in which they expect the package to arrive. AKA start: Today 2pm end Today: 3pm 
                        2. Players must guess an exact time of arrival e.g 11:27 or 14:44. Games can span multiple days
                        3. Guesses are accepted up to specified deadline. If no deadline is specified it will default to an hour after game creation, or five minutes before game start if there is not an hour between creation and delivery window
                        4. Guesses can be amended up until the guess deadline
                        5. In the event of a successful delivery. Closest time wins. The winner is awarded with one point
                        6. An additional BONUS point is awarded on guessing exact time of delivery
                        7. In the event of a DRAW e.g two players are an equal number of minutes from time of delivery. Points are split
                        8. If the addressee parcel arrives before the guess deadline. Game is void
                        9. If the addressee parcel arrives the day after the end of the delivery window. Game is void
                        10. The addressee may update the delivery window if it is delayed
                        11. Deliveroo, Uber Eats, Just Eat etc Deliveries are not allowed. Grocery deliveries (Sainsbury's, Ocado) are though
                        13. Sniping is permitted
                        14. You cannot submit a time already submitted
                        15. Any talk of collusion will be swiftly dealt with. Violently.
                    """
                            .trimIndent()
                }
            }
        }
    }
}
