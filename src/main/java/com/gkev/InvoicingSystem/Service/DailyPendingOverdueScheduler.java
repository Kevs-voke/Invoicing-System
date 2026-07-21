package com.gkev.InvoicingSystem.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DailyPendingOverdueScheduler {


    private final MainSchedulerService  mainSchedulerService;
    @PostConstruct
    public void init() {
        mainSchedulerService.scheduleJob(
                DailyPendingOverdueJob.class,
                "0 0 0 1 * ?",
                "dailyscheduler"
        );
    }
}
