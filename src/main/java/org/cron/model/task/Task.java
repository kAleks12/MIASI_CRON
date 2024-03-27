package org.cron.model.task;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

@Builder
@Data
public class Task implements RunnableTask {
    private static final Logger logger = LogManager.getLogger(Task.class);
    private String name;
    private String outputFile;
    private List<Job> jobs;


    @Override
    public void run() {
        for (var job : jobs) {
            try {
                job.run(outputFile);
            } catch (IOException  | InterruptedException e) {
                logger.error("Job in task '" + name + "' propagated error - " + e.getMessage());
            }
        }
    }
}
