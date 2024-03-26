package proj.cron.model.managed;

import lombok.Builder;
import lombok.Setter;
import proj.cron.model.raw.RunnableTask;
import proj.cron.model.raw.Task;

import static java.lang.Thread.sleep;

@Builder
@Setter
public class ManagedTask implements RunnableTask {
    private Task task;
    private RunConfig config;
    private boolean running;

    @Override
    public void run() {
        while (running) {
            if (config.shouldRun()) {
                task.run();
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
