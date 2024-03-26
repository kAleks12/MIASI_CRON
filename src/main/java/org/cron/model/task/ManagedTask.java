package org.cron.model.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.cron.model.config.RunConfig;

import static java.lang.Thread.sleep;

@Builder
@Setter
@Getter
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
