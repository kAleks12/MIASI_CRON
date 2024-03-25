package proj.cron.model.managed;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
public class CronRunConfig implements RunConfig {
    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    private String cronExpression;

    @Override
    public boolean shouldRun() {
        Cron cron = this.parser.parse(cronExpression);
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextExecution = executionTime.nextExecution(now).orElse(null);
        if (nextExecution == null) {
            return false;
        }
        return now.isEqual(nextExecution);
    }
}
