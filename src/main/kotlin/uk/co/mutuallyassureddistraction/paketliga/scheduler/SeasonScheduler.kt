package uk.co.mutuallyassureddistraction.paketliga.scheduler

import java.time.LocalDateTime
import org.quartz.impl.StdSchedulerFactory
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

class SeasonScheduler {
    fun scheduleEndOfSeason(): String {
        val logger = LoggerFactory.getLogger(SeasonScheduler::class.java)

        val yaml = Yaml()
        val inputStream = object {}.javaClass.classLoader.getResourceAsStream("scheduler-config.yml")
        val config: SeasonConfig = yaml.loadAs(inputStream, SeasonConfig::class.java)

        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val now = LocalDateTime.now()
        var seasonName: String? = ""

        for (season in config.seasons) {
            val endDateTime = LocalDateTime.parse(season.endDate)
            if (endDateTime.isBefore(now)) {
                logger.info("Skipping ${season.seasonName}: Already passed (${season.endDate})")
                continue
            }

            // TODO scheduling time
            seasonName = season.seasonName
            logger.info("Scheduling ${season.seasonName}: With end time(${season.endDate})")
            break
        }

        return "Season $seasonName"
    }
}
