package org.cron;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cron.grammar.cron_grammarLexer;
import org.cron.grammar.cron_grammarParser;
import org.cron.services.TaskVault;
import org.cron.services.Visitor;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
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

    public static void main(String[] args) throws Exception {
        // Load the input file
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_once.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_every.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_cron.kd");
//        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_run_at.kd");
        CharStream input = CharStreams.fromFileName("./src/main/resources/unit_combo.kd");

        // Create a lexer and parser
        Scanner scanner = new Scanner(System.in);
        logger.info("Press 'q' to quit");
        var vault = getTaskVault(input);
        vault.start();
        while (true) {
            String key = scanner.nextLine();
            if (key.equalsIgnoreCase("q")) {
                vault.stop();
                break;
            }
        }
    }
}
