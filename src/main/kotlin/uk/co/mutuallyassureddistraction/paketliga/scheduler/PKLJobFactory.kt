package uk.co.mutuallyassureddistraction.paketliga.scheduler

import dev.kordex.core.ExtensibleBot
import org.quartz.Job
import org.quartz.Scheduler
import org.quartz.spi.JobFactory
import org.quartz.spi.TriggerFiredBundle
import uk.co.mutuallyassureddistraction.paketliga.dao.GameDao

class PKLJobFactory(private val bot: ExtensibleBot, private val gameDao: GameDao) : JobFactory {
    override fun newJob(bundle: TriggerFiredBundle, scheduler: Scheduler): Job {
        val jobDetail = bundle.jobDetail
        val jobClass = jobDetail.jobClass

        if (jobClass == EndOfSeasonJob::class.java) {
            return EndOfSeasonJob(bot, gameDao)
        }

        return jobClass.getDeclaredConstructor().newInstance() as Job
    }
}
