package proj.cron.model.raw;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Data
@Builder
public class Job implements RunnableJob {
    private List<String> execs;
    private JobType type;

    @Override
    public void run(String outputFileName) throws IOException, InterruptedException {
        switch (type) {
            case CMD -> {
                runCmd(outputFileName);
                System.out.println("Command job completed");
            }
            case BASH -> {
                runBash(outputFileName);
                System.out.println("Bash job completed");
            }
            case PYTHON -> {
                runPython(outputFileName);
                System.out.println("Python job completed");
            }
        }

    }

    private void runCmd(String outputFileName) throws IOException, InterruptedException {
        // Run the command
        for (String exec : execs) {
            // Execute the command or script
            ProcessBuilder builder = new ProcessBuilder()
                    .command("bash", exec)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFileName)));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error, command '" + exec + "' exited with code: " + exitCode);
            }
        }
    }


    private void runBash(String outputFileName) throws IOException, InterruptedException {
        // Run the command
        for (String exec : execs) {
            // Execute the command or script
            ProcessBuilder builder = new ProcessBuilder()
                    .command(exec)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFileName)));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error, script '" + exec + "' exited with code: " + exitCode);
            }
        }
    }

    private void runPython(String outputFileName) throws IOException, InterruptedException {
        // Run the script
        for (String exec : execs) {
            // Execute the command or script
            ProcessBuilder builder = new ProcessBuilder()
                    .command("python3", exec)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFileName)));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error, python script '" + exec + "' exited with code: " + exitCode);
            }
        }
    }
}
