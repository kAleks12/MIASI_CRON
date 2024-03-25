package proj.cron.services;

import proj.cron.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import proj.cron.model.raw.Job;
import proj.cron.model.raw.JobType;
import proj.cron.model.raw.Task;

import java.util.*;

public class Visitor extends cron_grammarBaseVisitor<Object> {
    private final Map<String, Task> taskMap = new TreeMap<>();

    @Override
    public Object visitOption_list(cron_grammarParser.Option_listContext ctx) {
        // Returns a list of string args
        return Arrays.stream(ctx.STRING().get(0).toString().split(",")).toList();
    }

    @Override
    public Object visitJob(cron_grammarParser.JobContext ctx) {
        // Return generic job object, can be either a set of commands or a set of scripts
        List<String> execs = new ArrayList<>();
        if (ctx.cmds != null) {
           execs.addAll((List<String>) visit(ctx.cmds));
        }
        if (ctx.files != null) {
            execs.addAll((List<String>) visit(ctx.files));
        }
        var builder = Job.builder().execs(execs);
        if (ctx.cmds !=  null) {
            return builder.type(JobType.CMD)
                    .build();
        }
        var extensions = execs.stream()
                .map(e -> e.substring(e.lastIndexOf('.') + 1))
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
        if(this.taskMap.containsKey(taskName)) {
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
        System.out.println("Repeat once at date: " + ctx.DATE().getText() + " and time: " + ctx.TIME().getText());
        return visitChildren(ctx);
    }

    @Override
    public Object visitRepeat_every(cron_grammarParser.Repeat_everyContext ctx) {
        System.out.println("Repeat every " + ctx.INT().getText() + " " + ctx.TIME_UNIT().getText());
        return visitChildren(ctx);
    }

    @Override
    public Object visitRepeat_cron(cron_grammarParser.Repeat_cronContext ctx) {
        System.out.println("Repeat with cron: " + ctx.STRING().getText());
        return visitChildren(ctx);
    }

    @Override
    public Object visitTask_list(cron_grammarParser.Task_listContext ctx) {
        System.out.println("Tasks: " + ctx.STRING().stream().map(Objects::toString).toList());
        return visitChildren(ctx);
    }


    public static void main(String[] args) throws Exception {
        // Load the input file
        CharStream input = CharStreams.fromFileName("example.kd");

        // Create a lexer and parser
        cron_grammarLexer lexer = new cron_grammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cron_grammarParser parser = new cron_grammarParser(tokens);

        // Parse the input and get the parse tree
        ParseTree tree = parser.config();

        // Create an instance of the custom visitor and visit the parse tree
        Visitor visitor = new Visitor();
        visitor.visit(tree);
    }
}
