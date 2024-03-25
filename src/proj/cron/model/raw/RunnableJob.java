package proj.cron.model.raw;

import java.io.IOException;

public interface RunnableJob {
    void run(String outputFilename) throws IOException, InterruptedException;
}
