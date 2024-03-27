package org.cron.model.task;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cron.model.config.RunConfig;

import static java.lang.Thread.sleep;

@Builder
@Setter
@Getter
public class ManagedTask implements RunnableTask {
    private static final Logger logger = LogManager.getLogger(ManagedTask.class);
    private Task task;
    private RunConfig config;
    private boolean running;

    @Override
    public void run() {
        while (running) {
            if (config.shouldRun()) {
                logger.info("Starting task '" + task.getName() + "' with config " + config.toString());
                task.run();
                logger.info("Task '" + task.getName() + "' completed...");
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
