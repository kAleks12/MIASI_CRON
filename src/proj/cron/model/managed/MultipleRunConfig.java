package proj.cron.model.managed;

import java.time.ZonedDateTime;

public class MultipleRunConfig implements RunConfig {
    private final TimeUnit unit;
    private final Long value;
    private ZonedDateTime lastExecution = ZonedDateTime.now();

    public MultipleRunConfig(TimeUnit unit, Long value) {
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

        if (lastExecution.isBefore(edgeValue)) {
            lastExecution = now;
            return true;
        } else {
            return false;
        }
    }
}
