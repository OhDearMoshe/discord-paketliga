package uk.co.mutuallyassureddistraction.paketliga.scheduler

import java.time.LocalDateTime
import java.time.ZoneId
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml

class SeasonScheduler {
    fun scheduleEndOfSeason(pklScheduler: Scheduler): String {
        val logger = LoggerFactory.getLogger(SeasonScheduler::class.java)

        val yaml = Yaml()
        val inputStream = object {}.javaClass.classLoader.getResourceAsStream("scheduler-config.yml")
        val config: SeasonConfig = yaml.loadAs(inputStream, SeasonConfig::class.java)

        val now = LocalDateTime.now()
        var seasonName: String? = ""

        for (season in config.seasons) {
            seasonName = season.seasonName
            val endDateTime = LocalDateTime.parse(season.endDate)
            if (endDateTime.isBefore(now)) {
                logger.info("Skipping ${seasonName}: Already passed (${season.endDate})")
                continue
            }

            val jobKey = JobKey.jobKey(seasonName)
            if (pklScheduler.checkExists(jobKey)) {
                println("Job $seasonName already exists in database. Updating triggers...")
                pklScheduler.deleteJob(jobKey)
            }

            val jobDetail =
                JobBuilder.newJob(EndOfSeasonJob::class.java)
                    .withIdentity(jobKey)
                    .usingJobData("seasonName", seasonName)
                    .usingJobData("endDate", endDateTime.toString())
                    .build()

            val trigger =
                TriggerBuilder.newTrigger()
                    .withIdentity("$seasonName - Trigger")
                    .startAt(endDateTime.atZone(ZoneId.of("Europe/London")).toInstant())
                    .build()

            pklScheduler.scheduleJob(jobDetail, trigger)
            logger.info("Scheduling ${seasonName}: With end time(${season.endDate})")
            break
        }

        return "Season $seasonName"
    }
}
