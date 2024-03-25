package proj.cron.interpreter;

import proj.cron.grammar.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Arrays;
import java.util.Objects;

public class Visitor extends cron_grammarBaseVisitor<Object> {
    @Override
    public Object visitOption_list(cron_grammarParser.Option_listContext ctx) {
        // Returns a list of string args
        return Arrays.stream(ctx.STRING().get(0).toString().split(",")).toList();
    }

    @Override
    public Object visitTask_definition(cron_grammarParser.Task_definitionContext ctx) {
        System.out.println("Task name: " + ctx.name.getText());
        var options = visit(ctx.files);

        if (ctx.cmds != null) {
            System.out.println("Task cmds: " + visit(ctx.cmds));
        }
        if (ctx.files != null) {
            System.out.println("Task files: " + visit(ctx.files));
        }
        System.out.println("Task output: " + ctx.out.getText());
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
