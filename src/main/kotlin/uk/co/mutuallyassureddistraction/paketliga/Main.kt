package uk.co.mutuallyassureddistraction.paketliga

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.slf4j.LoggerFactory
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.co.mutuallyassureddistraction.paketliga.dao.GuessDao
import uk.co.mutuallyassureddistraction.paketliga.dao.PointDao
import uk.co.mutuallyassureddistraction.paketliga.dao.WinDao
import uk.co.mutuallyassureddistraction.paketliga.extensions.*
import uk.co.mutuallyassureddistraction.paketliga.matching.*
import uk.co.mutuallyassureddistraction.paketliga.matching.results.GameResultResolver
import uk.co.mutuallyassureddistraction.paketliga.matching.results.PointUpdaterService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.DeliveryTimeParser
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GameTimeParserService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTimeParserService
import uk.co.mutuallyassureddistraction.paketliga.matching.time.TimeParser
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GameValidator
import uk.co.mutuallyassureddistraction.paketliga.matching.validators.GuessValidator
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

val PG_JDBC_URL = env("POSTGRES_JDBC_URL")
val PG_USERNAME = env("POSTGRES_USERNAME")
val PG_PASSWORD = env("POSTGRES_PASSWORD")
val SERVER_ID = Snowflake(
    env("SERVER_ID").toLong()  // Get the test server ID from the env vars or a .env file
)

private val BOT_TOKEN = env("BOT_TOKEN") // Get the bot' token from the env vars or a .env file
private class PKLBOT {}

suspend fun main(args: Array<String>) {

    TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));

    val logger = LoggerFactory.getLogger(PKLBOT::class.java)
    logger.info("Initialising database")
    val connection = DriverManager.getConnection(PG_JDBC_URL, PG_USERNAME, PG_PASSWORD)
    if(connection.isValid(0)) {
        val jdbi = getJdbi(connection)
        logger.info("Init DAO's")
        // initialise GameDao and GameExtension
        val gameDao = jdbi.onDemand<GameDao>()
        val guessDao = jdbi.onDemand<GuessDao>()
        val pointDao = jdbi.onDemand<PointDao>()
        val winDao = jdbi.onDemand<WinDao>()
        logger.info("Init Services")
        //Validators
        val gameValidator = GameValidator()
        val guessValidator = GuessValidator()
        // Time parsers
        val gameTimeParserService = GameTimeParserService(TimeParser())
        val guessTimeParserService = GuessTimeParserService(TimeParser())
        val deliveryTimeParser = DeliveryTimeParser(TimeParser())

        val gameFinderService = GameFinderService(gameDao)
        val guessUpsertService = GuessUpsertService(guessDao, gameDao, guessValidator)
        val guessFinderService = GuessFinderService(guessDao)
        val gameUpsertService = GameUpsertService(gameDao, guessFinderService, gameValidator)
        val gameResultResolver = GameResultResolver()
        val pointUpdaterService = PointUpdaterService(pointDao, winDao)
        val gameEndService = GameEndService(guessDao, gameDao, gameResultResolver, pointUpdaterService)
        val leaderboardService = LeaderboardService(pointDao)
        val voidGameService = VoidGameService(gameDao)


        logger.info("Creating Extensions")
        val createGameExtension = CreateGameExtension(gameUpsertService, gameTimeParserService, SERVER_ID)
        val updateGameExtension = UpdateGameExtension(gameUpsertService, gameTimeParserService, SERVER_ID)
        val findGamesExtension = FindGamesExtension(gameFinderService, SERVER_ID)
        val guessGameExtension = GuessGameExtension(guessUpsertService, guessTimeParserService, SERVER_ID)
        val findGuessExtension = FindGuessExtension(guessFinderService, SERVER_ID)
        val endGameExtension = EndGameExtension(gameEndService, deliveryTimeParser, SERVER_ID)
        val leaderboardExtension = LeaderboardExtension(leaderboardService, SERVER_ID)
        val helpExtension = HelpExtension(SERVER_ID)
        val voidGameExtension = VoidGameExtension(voidGameService, SERVER_ID)
        val contributeExtension = ContributeExtension(SERVER_ID)
        val rulesExtension = RulesExtension(SERVER_ID)
        val creditsExtension = CreditsExtension(SERVER_ID)


        logger.info("Creating bot")
        val bot = ExtensibleBot(BOT_TOKEN) {
            chatCommands {
                enabled = true
                prefix { _ -> "?" }
            }

            extensions {
                add { createGameExtension }
                add { updateGameExtension }
                add { findGamesExtension }
                add { guessGameExtension }
                add { findGuessExtension }
                add { endGameExtension }
                add { leaderboardExtension }
                add { helpExtension }
                add { voidGameExtension }
                add { contributeExtension }
                add { rulesExtension }
                add { creditsExtension }
            }
        }
        logger.info("Starting Bot. Beep boop")
        bot.start()
    }

    // TODO logging when DB is timeout
}

private fun getJdbi(connection: Connection): Jdbi {
    return Jdbi.create(connection)
        .installPlugin(PostgresPlugin())
        .installPlugin(SqlObjectPlugin())
        .installPlugin(KotlinPlugin())
        .installPlugin(KotlinSqlObjectPlugin())
}
