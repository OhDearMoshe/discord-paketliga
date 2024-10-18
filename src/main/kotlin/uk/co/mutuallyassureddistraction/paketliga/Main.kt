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
import java.sql.Connection
import java.sql.DriverManager
import kotlin.math.log

val PG_JDBC_URL = env("POSTGRES_JDBC_URL")
val PG_USERNAME = env("POSTGRES_USERNAME")
val PG_PASSWORD = env("POSTGRES_PASSWORD")
val SERVER_ID = Snowflake(
    env("SERVER_ID").toLong()  // Get the test server ID from the env vars or a .env file
)

private val BOT_TOKEN = env("BOT_TOKEN") // Get the bot' token from the env vars or a .env file
private class PKLBOT {}

suspend fun main(args: Array<String>) {
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
        val gameFinderService = GameFinderService(gameDao)
        val guessUpsertService = GuessUpsertService(guessDao, gameDao)
        val guessFinderService = GuessFinderService(guessDao)
        val gameUpsertService = GameUpsertService(gameDao, guessFinderService)
        val gameResultResolver = GameResultResolver()
        val gameEndService = GameEndService(guessDao, gameDao, pointDao, winDao, gameResultResolver)
        val leaderboardService = LeaderboardService(pointDao)
        val gameTimeParserService = GameTimeParserService()

        val createGameExtension = CreateGameExtension(gameUpsertService, gameTimeParserService, SERVER_ID)
        val updateGameExtension = UpdateGameExtension(gameUpsertService, SERVER_ID)
        val findGamesExtension = FindGamesExtension(gameFinderService, SERVER_ID)
        val guessGameExtension = GuessGameExtension(guessUpsertService, SERVER_ID)
        val findGuessExtension = FindGuessExtension(guessFinderService, SERVER_ID)
        val endGameExtension = EndGameExtension(gameEndService, SERVER_ID)
        val leaderboardExtension = LeaderboardExtension(leaderboardService, SERVER_ID)

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
