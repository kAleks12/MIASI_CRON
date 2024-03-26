package org.cron.model.config;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class CronRunConfig implements RunConfig {
    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    @Getter
    private final String cronExpression;
    private ZonedDateTime nextExecution = null;


    @Override
    public boolean shouldRun() {
        Cron cron = this.parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        if (this.nextExecution == null) {
            this.nextExecution = executionTime.nextExecution(now).orElse(null);
            return false;
        }
        if (now.isAfter(this.nextExecution) || now.isEqual(this.nextExecution)) {
            this.nextExecution = executionTime.nextExecution(now).orElse(null);
            return true;
        }
        return false;
    }
}
