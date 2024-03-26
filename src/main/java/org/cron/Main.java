package org.cron;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.cron.grammar.cron_grammarLexer;
import org.cron.grammar.cron_grammarParser;
import org.cron.services.TaskVault;
import org.cron.services.Visitor;

import java.util.Scanner;

public class Main {
    private static TaskVault getTaskVault(CharStream input) {
        cron_grammarLexer lexer = new cron_grammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cron_grammarParser parser = new cron_grammarParser(tokens);

        // Parse the input and get the parse tree
        ParseTree tree = parser.config();

        // Create an instance of the custom visitor and visit the parse tree
        Visitor visitor = new Visitor();
        visitor.visit(tree);
        var vault = visitor.getTaskVault();
        vault.start();
        return vault;
    }

    public static void main(String[] args) throws Exception {
        // Load the input file
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_once.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_every.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_cron.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_at.kd");
        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_combo.kd");

        // Create a lexer and parser
        var vault = getTaskVault(input);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press 'q' to quit");
        while (true) {
            String key = scanner.nextLine();
            if (key.equalsIgnoreCase("q")) {
                vault.stop();
                break;
            }
        }
    }
}
