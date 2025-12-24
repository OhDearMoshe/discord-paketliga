package uk.co.mutuallyassureddistraction.paketliga.scheduler

import dev.kordex.core.ExtensibleBot
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao

@DisallowConcurrentExecution
class EndOfSeasonJob(private val bot: ExtensibleBot, private val gameDao: GameDao) : Job {
    override fun execute(p0: JobExecutionContext) {
        println(p0.jobDetail.jobDataMap["seasonName"])
        gameDao.findActiveGameById(1)
    }
}
