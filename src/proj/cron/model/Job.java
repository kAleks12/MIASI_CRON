package proj.cron.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Job {
    private List<String> execs;
    private JobType type;
}
