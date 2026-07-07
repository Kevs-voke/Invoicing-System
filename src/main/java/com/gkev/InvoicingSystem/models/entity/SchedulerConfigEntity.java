package com.gkev.InvoicingSystem.models.entity;

import lombok.Builder;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("scheduler_config")
public class SchedulerConfigEntity {

    @Id
    private Long id;

    @Column("job_name")
    private String jobName;

    @Column("group_name")
    private String groupName;

    @Column("cron_expression")
    private String cronExpression;

    @Column("enabled")
    private boolean enabled;
}