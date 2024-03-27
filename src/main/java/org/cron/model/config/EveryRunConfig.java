package org.cron.model.config;


import lombok.Getter;
import org.cron.model.task.TimeUnit;

import java.time.ZonedDateTime;

public class EveryRunConfig implements RunConfig {
    @Getter
    private final TimeUnit unit;
    @Getter
    private final Long value;
    private ZonedDateTime lastExecution;

    public EveryRunConfig(TimeUnit unit, Long value) {
        this.unit = unit;
        this.value = value;
    }

    @Override
    public boolean shouldRun() {
        var now = ZonedDateTime.now();
        ZonedDateTime edgeValue;
        switch (unit) {
            case SECOND -> edgeValue = now.minusSeconds(this.value);
            case MINUTE -> edgeValue = now.minusMinutes(this.value);
            case HOUR ->  edgeValue = now.minusHours(this.value);
            case DAY -> edgeValue = now.minusDays(this.value);
            case MONTH -> edgeValue = now.minusMonths(this.value);
            default -> throw new IllegalArgumentException("Invalid time unit");
        }
        if (lastExecution == null) {
            lastExecution = now;
            return true;
        }

        if (lastExecution.isBefore(edgeValue)) {
            lastExecution = now;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "EveryRunConfig( unit = '" + unit + "', value = '" + value + "' )";
    }
}
