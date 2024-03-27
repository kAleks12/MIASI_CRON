package org.cron.services;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cron.model.task.ManagedTask;

import java.util.ArrayList;
import java.util.List;

public class TaskVault {
    private static final Logger logger = LogManager.getLogger(TaskVault.class);
    @Getter
    private final List<ManagedTask> tasks = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    public void addTask(ManagedTask task) {
        tasks.add(task);
    }

    public Integer getTaskCount() {
        return tasks.size();
    }

    public void start() {
        tasks.forEach(task -> threads.add(new Thread(task::run)));
        threads.forEach(Thread::start);
        logger.info("Initialized vault");
    }

    public void stop() throws InterruptedException {
        tasks.forEach(task -> task.setRunning(false));
        logger.info("Stopping vault");
        for (Thread thread : threads) {
            thread.join();
        }
        logger.info("Vault stopped");
    }
}
