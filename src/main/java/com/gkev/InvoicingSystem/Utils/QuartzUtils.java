package com.gkev.InvoicingSystem.Utils;

import com.gkev.InvoicingSystem.models.DTO.TriggerInfoDTO;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Component
public class QuartzUtils {
//    Build the Job Detail
    public JobDetail buildJobDetail(Class<? extends  Job> className, String groupName) {
        return JobBuilder
                .newJob(className)
                .withIdentity(className.getSimpleName(), groupName)
                .storeDurably(true)
                .requestRecovery(true)
                .build();

    }

//    Build the trigger
    public Trigger buildTrigger(Class<? extends Job> className, String expression){
        return TriggerBuilder
                .newTrigger()
                .withIdentity(className.getSimpleName())
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build();
    }

//    Build trigger Info Object
public TriggerInfoDTO buildTriggerInfoObj(int triggerCount,
                                     boolean runForever,
                                     long repeatValue,
                                     long initialOffset,
                                     String information) {
    return new TriggerInfoDTO(

            triggerCount,
            runForever,
            repeatValue,
            initialOffset,
            information
    );
}


}
