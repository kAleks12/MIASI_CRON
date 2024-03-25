package proj.cron.services;

import lombok.Setter;
import proj.cron.model.managed.ManagedTask;

import java.util.ArrayList;
import java.util.List;

@Setter
public class TaskVault {
    private List<ManagedTask> tasks = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    public void addTask(ManagedTask task) {
        tasks.add(task);
    }

    public void start() {
        tasks.forEach(task -> threads.add(new Thread(task::run)));
        threads.forEach(Thread::start);
    }

    public void stop() throws InterruptedException {
        tasks.forEach(task -> task.setRunning(false));
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
