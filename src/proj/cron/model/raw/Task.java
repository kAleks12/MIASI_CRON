package proj.cron.model.raw;

import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.util.List;

@Builder
@Data
public class Task implements RunnableTask {
    private String name;
    private String outputFile;
    private List<Job> jobs;


    @Override
    public void run() {
        for (var job : jobs) {
            try {
                job.run(outputFile);
            } catch (IOException  | InterruptedException e) {
                System.out.println("Error running task " + name + " detail: " + e.getMessage());
            }
            System.out.println("Task " + name + " completed");
        }
    }
}
