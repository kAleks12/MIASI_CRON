package org.cron.model.config;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class AtRunConfig implements RunConfig {
    private final ZonedDateTime nextExecution;
    private Integer runCounter = 1;

    public AtRunConfig(ZonedDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }

    @Override
    public boolean shouldRun() {
        if (this.runCounter <  1) {
            return false;
        }
        ZonedDateTime now = ZonedDateTime.now();
        if (now.isAfter(this.nextExecution) || now.isEqual(this.nextExecution)) {
            this.runCounter--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AtRunConfig( datetime = " + nextExecution + " )";
    }
}
