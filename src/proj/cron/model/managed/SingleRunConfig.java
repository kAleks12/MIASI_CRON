package proj.cron.model.managed;

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
}
