package proj.cron.model.task;

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
            case CMD -> runCmd(outputFileName);
            case BASH -> runBash(outputFileName);
            case PYTHON -> runPython(outputFileName);
        }

    }

    private void runCmd(String outputFileName) throws IOException, InterruptedException {
        // Run the command
        for (String exec : execs) {
            // Execute the command or script
            ProcessBuilder builder = new ProcessBuilder()
                    .command("powershell.exe", "-Command", exec) // Specify PowerShell as the command
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFileName)));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error, command '" + exec + "' exited with code: " + exitCode);
            } else {
                System.out.println("Command '" + exec + "' executed successfully");
            }
        }
    }


    private void runBash(String outputFileName) throws IOException, InterruptedException {
        // Run the command
        for (String exec : execs) {
            // Execute the command or script
            ProcessBuilder builder = new ProcessBuilder()
                    .command("powershell.exe", "-Command", "bash " + exec)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFileName)));
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error, script '" + exec + "' exited with code: " + exitCode);
            } else {
                System.out.println("Script '" + exec + "' executed successfully");
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
            } else {
                System.out.println("Python script '" + exec + "' executed successfully");
            }
        }
    }
}
