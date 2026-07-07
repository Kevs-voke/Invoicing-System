package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Exceptions.JobSchedulingException;
import com.gkev.InvoicingSystem.Utils.QuartzUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainSchedulerService {
    private final Scheduler scheduler;
    private final QuartzUtils quartzUtils;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void startScheduler() {
        try {
            scheduler.start();
        }catch (SchedulerException e){
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void closeScheduler(){
        try{
            scheduler.shutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleJob(Class<? extends Job> className, String cronExpression, String groupName) {
        try {
            JobDetail jobDetail = quartzUtils.buildJobDetail(className, groupName);
            Trigger trigger = quartzUtils.buildTrigger(className, cronExpression);

            JobKey jobKey = jobDetail.getKey();
            if (scheduler.checkExists(jobKey)) {
                logger.info("Job {} already exists in JobStore, skipping re-registration", jobKey);
                return;
            }

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new JobSchedulingException(
                    "JOB_SCHEDULING_FAILED",
                    "Failed to schedule job: " + className.getSimpleName(),
                    e
            );
        }
    }

}
