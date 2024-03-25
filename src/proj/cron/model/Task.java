package proj.cron.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Task {
    private String name;
    private String outputFile;
    private List<Job> jobs;
}
