package org.cron.services;

import lombok.Getter;
import org.cron.grammar.*;
import org.cron.model.config.CronRunConfig;
import org.cron.model.config.MultipleRunConfig;
import org.cron.model.config.SingleRunConfig;
import org.cron.model.task.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Visitor extends cron_grammarBaseVisitor<Object> {
    @Getter
    private final TaskVault taskVault = new TaskVault();
    private final Map<String, Task> taskMap = new TreeMap<>();
    private final Integer maxTasks = 20;


    @Override
    public Object visitOption_list(cron_grammarParser.Option_listContext ctx) {
        // Returns a list of string args
        return Arrays.stream(ctx.STRING().get(0).toString().split(",")).toList();
    }

    @Override
    public Object visitTask_list(cron_grammarParser.Task_listContext ctx) {
        return Arrays.stream(ctx.STRING().get(0).toString().split(",")).toList();
    }

    @Override
    public Object visitJob(cron_grammarParser.JobContext ctx) {
        // Return generic job object, can be either a set of commands or a set of scripts
        List<String> execs = new ArrayList<>();
        if (ctx.cmds != null) {
            execs.addAll(mapAndGetStrings(visit(ctx.cmds)));
        }
        if (ctx.files != null) {
            execs.addAll(mapAndGetStrings(visit(ctx.files)));
        }
        var builder = Job.builder().execs(execs);
        if (ctx.cmds != null) {
            return builder.type(JobType.CMD)
                    .build();
        }
        var extensions = execs.stream()
                .map(e -> e.substring(e.lastIndexOf('.') + 1))
                .map(e -> e.replace("\"", ""))
                .toList();

        if (extensions.stream().allMatch(e -> e.equals("sh"))) {
            builder.type(JobType.BASH);
        } else if (extensions.stream().allMatch(e -> e.equals("py"))) {
            builder.type(JobType.PYTHON);
        } else {
            throw new RuntimeException("Unsupported file extension or mixed file types in job definition");
        }
        return builder.build();
    }

    @Override
    public Object visitTask_definition(cron_grammarParser.Task_definitionContext ctx) {
        var jobList = ctx.job().stream().map(obj -> (Job) visit(obj)).toList();
        if (jobList.isEmpty()) {
            throw new RuntimeException("Task definition must have at least one job (cmds or files block)");
        }
        String taskName = ctx.name.getText().replace("\"", "");
        if (this.taskMap.containsKey(taskName)) {
            throw new RuntimeException("Task with name " + taskName + " already exists");
        }

        String output = ctx.out != null ? ctx.out.getText().replace("\"", "") : "output.txt";
        var newTask = Task.builder()
                .name(taskName)
                .outputFile(output)
                .jobs(jobList)
                .build();
        this.taskMap.put(taskName, newTask);
        return visitChildren(ctx);
    }

    @Override
    public Object visitRepeat_once(cron_grammarParser.Repeat_onceContext ctx) {
        var tasks = getAndCheckTasks(visit(ctx.task_list()));
        tasks.forEach(task -> {
            var taskObj = taskMap.get(task);
            var managedTask = ManagedTask.builder()
                    .task(taskObj)
                    .config(new SingleRunConfig())
                    .running(true)
                    .build();
            taskVault.addTask(managedTask);
        });
        return visitChildren(ctx);
    }

    @Override
    public Object visitRepeat_every(cron_grammarParser.Repeat_everyContext ctx) {
        var tasks = getAndCheckTasks(visit(ctx.task_list()));
        var unit = TimeUnit.valueOf(ctx.TIME_UNIT().getText().toUpperCase());
        var value = Long.parseLong(ctx.INT().getText());
        tasks.forEach(task -> {
            var taskObj = taskMap.get(task);
            var config = new MultipleRunConfig(unit, value);
            var managedTask = ManagedTask.builder()
                    .task(taskObj)
                    .config(config)
                    .running(true)
                    .build();
            taskVault.addTask(managedTask);
        });
        return visitChildren(ctx);
    }

    @Override
    public Object visitRepeat_cron(cron_grammarParser.Repeat_cronContext ctx) {
        var tasks = getAndCheckTasks(visit(ctx.task_list()));
        var cron = ctx.STRING().getText().replace("\"", "");
        tasks.forEach(task -> {
            var taskObj = taskMap.get(task);
            var managedTask = ManagedTask.builder()
                    .task(taskObj)
                    .config(new CronRunConfig(cron))
                    .running(true)
                    .build();
            taskVault.addTask(managedTask);
        });
        return visitChildren(ctx);
    }

    @Override
    public Object visitRun_configuration(cron_grammarParser.Run_configurationContext ctx) {
        Object result = visitChildren(ctx);
        if(this.taskVault.getTaskCount() > this.maxTasks) {
            throw new RuntimeException("Cannot define more than " + this.maxTasks + " tasks in a single configuration file");
        }
        return result;
    }

    private List<String> getAndCheckTasks(Object obj) {
        var tasks = mapAndGetStrings(obj);
        //Check whether all tasks exist in taskMap
        tasks.forEach(
                task -> taskMap.computeIfAbsent(task, key -> {
                    throw new RuntimeException("Task with name '" + task + "' was not declared");
                })
        );
        return tasks;
    }

    private List<String> mapAndGetStrings(Object obj) {
        return ((List<String>) obj).stream()
                .map(e -> e.replace("\"", ""))
                .map(String::strip)
                .toList();
    }
}
