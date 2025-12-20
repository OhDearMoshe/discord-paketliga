package uk.co.mutuallyassureddistraction.paketliga.scheduler

import java.util.Properties
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory

class PKLSchedulerFactory {
    fun createScheduler(pklJobFactory: PKLJobFactory): Scheduler {
        val props = Properties()

        props["org.quartz.scheduler.instanceName"] = "PKLScheduler"
        props["org.quartz.scheduler.instanceId"] = "AUTO"

        props["org.quartz.threadPool.class"] = "org.quartz.simpl.SimpleThreadPool"
        props["org.quartz.threadPool.threadCount"] = "5"

        props["org.quartz.jobStore.class"] = "org.quartz.impl.jdbcjobstore.JobStoreTX"
        props["org.quartz.jobStore.driverDelegateClass"] = "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate"
        props["org.quartz.jobStore.dataSource"] = "PKLDataSource"
        props["org.quartz.jobStore.useProperties"] = "true"

        props["org.quartz.dataSource.PKLDataSource.provider"] = "hikaricp"
        props["org.quartz.dataSource.PKLDataSource.driver"] = "org.postgresql.Driver"
        props["org.quartz.dataSource.PKLDataSource.URL"] = System.getenv("POSTGRES_JDBC_URL")
        props["org.quartz.dataSource.PKLDataSource.user"] = System.getenv("POSTGRES_USERNAME")
        props["org.quartz.dataSource.PKLDataSource.password"] = System.getenv("POSTGRES_PASSWORD")
        props["org.quartz.dataSource.PKLDataSource.maxConnections"] = "10"

        val scheduler = StdSchedulerFactory(props).scheduler
        scheduler.setJobFactory(pklJobFactory)
        scheduler.start()
        return scheduler
    }
}
