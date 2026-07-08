package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.models.entity.SchedulerConfigEntity;
import com.gkev.InvoicingSystem.models.repo.SchedulerConfigRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyOverdueScheduler {

    private static final String DEFAULT_CRON = "0 53 16 * * ?";

    private final MainSchedulerService mainScheduler;
    private final SchedulerConfigRepo schedulerConfigRepo;

    @PostConstruct
    public void init() {

        SchedulerConfigEntity schedulerConfig = schedulerConfigRepo
                .findByJobName("DailyOverdueSchedulerJob")
                .blockOptional()
                .orElseGet(() -> {
                    SchedulerConfigEntity entity = SchedulerConfigEntity.builder()
                            .jobName("DailyOverdueSchedulerJob")
                            .groupName("dailyscheduler")
                            .cronExpression(DEFAULT_CRON)
                            .enabled(true)
                            .build();

                    return schedulerConfigRepo.save(entity).block();
                });

        if (schedulerConfig == null) {
            throw new IllegalStateException("Failed to initialize scheduler configuration.");
        }

        if (schedulerConfig.isEnabled()) {
            mainScheduler.scheduleJob(
                    DailySchedulerOverdueJob.class,
                    schedulerConfig.getCronExpression(),
                    schedulerConfig.getGroupName()
            );
        }
    }
}
