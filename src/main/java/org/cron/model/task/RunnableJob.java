package org.cron.model.task;

import java.io.IOException;

public interface RunnableJob {
    void run(String outputFilename) throws IOException, InterruptedException;
}
