package org.cron.model.config;

public class SingleRunConfig implements RunConfig {
    private boolean shouldRun = true;
    @Override
    public boolean shouldRun() {
        if (shouldRun) {
            shouldRun = false;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SingleRunConfig()";
    }
}
