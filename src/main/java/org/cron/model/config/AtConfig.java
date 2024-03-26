package org.cron.model.config;

import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
public class AtConfig implements RunConfig {
    private ZonedDateTime nextExecution;
    @Override
    public boolean shouldRun() {
        if (nextExecution == null) {
            return false;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(this.nextExecution) || now.isEqual(this.nextExecution)) {
            nextExecution = null;
            return true;
        } else {
            return false;
        }
    }
}
