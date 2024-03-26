package org.cron.services;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cron.grammar.cron_grammarLexer;
import org.cron.grammar.cron_grammarParser;
import org.cron.model.config.AtConfig;
import org.cron.model.config.CronRunConfig;
import org.cron.model.config.MultipleRunConfig;
import org.cron.model.config.SingleRunConfig;
import org.cron.model.task.JobType;
import org.cron.model.task.ManagedTask;
import org.cron.model.task.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class VisitorTest {
    private static List<ManagedTask> tasks;

    @BeforeAll
    public static void setup() throws IOException {
        CharStream input = CharStreams.fromFileName("./src/test/resources/test.kd");
        tasks = getTaskVault(input).getTasks();
    }

    @Test
    public void testTaskNumber() {
        // Check whether the number of managed tasks is correct
        assertEquals(4, tasks.size());
    }

    @Test
    public void testRepeatOnce() {
        var testObj = tasks.get(0);

        var config = testObj.getConfig();
        var task = testObj.getTask();
        var jobs = task.getJobs();

        //Check correct config and task base attributes
        assertEquals(SingleRunConfig.class, config.getClass());
        assertEquals("example1", task.getName());
        assertEquals("./output.txt", task.getOutputFile());
        assertEquals(3, jobs.size());

        //Check correct job attributes
        assertEquals(JobType.CMD, jobs.get(0).getType());
        assertEquals("echo hello from terminal", jobs.get(0).getExecs().get(0));
        assertEquals(1, jobs.get(0).getExecs().size());

        assertEquals(JobType.BASH, jobs.get(1).getType());
        assertEquals("./script.sh", jobs.get(1).getExecs().get(0));
        assertEquals(1, jobs.get(1).getExecs().size());

        assertEquals(JobType.PYTHON, jobs.get(2).getType());
        assertEquals("./main.py", jobs.get(2).getExecs().get(0));
        assertEquals(1, jobs.get(2).getExecs().size());
    }

    @Test
    public void testRepeatAt() {
        var testObj = tasks.get(1);

        var config = testObj.getConfig();
        var task = testObj.getTask();
        var jobs = task.getJobs();

        //Check correct config and task base attributes
        assertEquals(AtConfig.class, config.getClass());
        assertEquals("example1", task.getName());
        assertEquals("./output.txt", task.getOutputFile());
        assertEquals(3, jobs.size());

        //Check correct config attributes
        var expectedExecution = ZonedDateTime.parse("2024-03-20T11:00:00" + ZonedDateTime.now().getOffset());
        assertEquals(expectedExecution, ((AtConfig) config).getNextExecution());

        //Check correct job attributes
        assertEquals(JobType.CMD, jobs.get(0).getType());
        assertEquals("echo hello from terminal", jobs.get(0).getExecs().get(0));
        assertEquals(1, jobs.get(0).getExecs().size());

        assertEquals(JobType.BASH, jobs.get(1).getType());
        assertEquals("./script.sh", jobs.get(1).getExecs().get(0));
        assertEquals(1, jobs.get(1).getExecs().size());

        assertEquals(JobType.PYTHON, jobs.get(2).getType());
        assertEquals("./main.py", jobs.get(2).getExecs().get(0));
        assertEquals(1, jobs.get(2).getExecs().size());
    }

    @Test
    public void testRepeatEvery() {
        var testObj = tasks.get(2);

        var config = testObj.getConfig();
        var task = testObj.getTask();
        var jobs = task.getJobs();

        //Check correct config and task base attributes
        assertEquals(MultipleRunConfig.class, config.getClass());
        assertEquals("example1", task.getName());
        assertEquals("./output.txt", task.getOutputFile());
        assertEquals(3, jobs.size());

        //Check correct config attributes
        assertEquals(30, ((MultipleRunConfig) config).getValue());
        assertEquals(TimeUnit.MINUTE, ((MultipleRunConfig) config).getUnit());

        //Check correct job attributes
        assertEquals(JobType.CMD, jobs.get(0).getType());
        assertEquals("echo hello from terminal", jobs.get(0).getExecs().get(0));
        assertEquals(1, jobs.get(0).getExecs().size());

        assertEquals(JobType.BASH, jobs.get(1).getType());
        assertEquals("./script.sh", jobs.get(1).getExecs().get(0));
        assertEquals(1, jobs.get(1).getExecs().size());

        assertEquals(JobType.PYTHON, jobs.get(2).getType());
        assertEquals("./main.py", jobs.get(2).getExecs().get(0));
        assertEquals(1, jobs.get(2).getExecs().size());
    }


    @Test
    public void testRepeatCron() {
        var testObj = tasks.get(3);

        var config = testObj.getConfig();
        var task = testObj.getTask();
        var jobs = task.getJobs();

        //Check correct config and task base attributes
        assertEquals(CronRunConfig.class, config.getClass());
        assertEquals("example1", task.getName());
        assertEquals("./output.txt", task.getOutputFile());
        assertEquals(3, jobs.size());

        //Check correct config attributes
        assertEquals("0 0 * * *", ((CronRunConfig) config).getCronExpression());

        //Check correct job attributes
        assertEquals(JobType.CMD, jobs.get(0).getType());
        assertEquals("echo hello from terminal", jobs.get(0).getExecs().get(0));
        assertEquals(1, jobs.get(0).getExecs().size());

        assertEquals(JobType.BASH, jobs.get(1).getType());
        assertEquals("./script.sh", jobs.get(1).getExecs().get(0));
        assertEquals(1, jobs.get(1).getExecs().size());

        assertEquals(JobType.PYTHON, jobs.get(2).getType());
        assertEquals("./main.py", jobs.get(2).getExecs().get(0));
        assertEquals(1, jobs.get(2).getExecs().size());
    }

    private static TaskVault getTaskVault(CharStream input) {
        cron_grammarLexer lexer = new cron_grammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cron_grammarParser parser = new cron_grammarParser(tokens);

        // Parse the input and get the parse tree
        ParseTree tree = parser.config();

        // Create an instance of the custom visitor and visit the parse tree
        Visitor visitor = new Visitor();
        visitor.visit(tree);
        return visitor.getTaskVault();
    }

}