package uk.co.mutuallyassureddistraction.paketliga.scheduler

import dev.kordex.core.ExtensibleBot
import io.mockk.mockk
import io.mockk.verify
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.quartz.Scheduler
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension

@Testcontainers
@ExtendWith(SystemStubsExtension::class)
class SeasonSchedulerIntegrationTest {

    companion object {
        @Container val postgres = PostgreSQLContainer("postgres:16-alpine")
    }

    @SystemStub private lateinit var envVars: EnvironmentVariables

    private lateinit var scheduler: Scheduler
    private lateinit var pklJobFactory: PKLJobFactory
    private lateinit var seasonScheduler: SeasonScheduler
    private lateinit var gameDao: GameDao

    @BeforeEach
    fun setUp() {
        val dynamicTime =
            LocalDateTime.now().plusSeconds(20).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

        val testYaml =
            """
            seasons:
              - seasonName: "Test Season"
                endDate: "$dynamicTime"
            """
                .trimIndent()

        // Write to the build folder where ClassLoader looks
        val resourcePath = Paths.get("build/resources/test/scheduler-config.yml")
        Files.createDirectories(resourcePath.parent)
        Files.writeString(resourcePath, testYaml)

        Flyway.configure()
            .dataSource(postgres.jdbcUrl, postgres.username, postgres.password)
            .locations("filesystem:.//migrations")
            .load()
            .migrate()

        envVars.set("POSTGRES_JDBC_URL", postgres.jdbcUrl)
        envVars.set("POSTGRES_USERNAME", postgres.username)
        envVars.set("POSTGRES_PASSWORD", postgres.password)

        gameDao = mockk<GameDao>(relaxed = true)
        val bot = mockk<ExtensibleBot>()
        pklJobFactory = PKLJobFactory(bot, gameDao)
        scheduler = PKLSchedulerFactory().createScheduler(pklJobFactory)
        seasonScheduler = SeasonScheduler()
    }

    @Test
    fun `should migrate schema and execute job`() {
        seasonScheduler.scheduleEndOfSeason(scheduler)
        await().atMost(40, TimeUnit.SECONDS).untilAsserted { verify(exactly = 1) { gameDao.findActiveGameById(1) } }
    }
}
