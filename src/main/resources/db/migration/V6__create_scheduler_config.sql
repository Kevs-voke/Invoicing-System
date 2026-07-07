CREATE TABLE scheduler_config (
                          id BIGSERIAL PRIMARY KEY,
                          job_name VARCHAR(200) NOT NULL,
                          group_name VARCHAR(200) NOT NULL,
                          cron_expression VARCHAR(120) NOT NULL,
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,

                          CONSTRAINT uq_scheduler_config_job_group
                              UNIQUE (job_name, group_name)
);