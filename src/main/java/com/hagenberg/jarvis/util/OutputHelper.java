package com.hagenberg.jarvis.util;

public class OutputHelper {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_ORANGE = "\u001B[38;5;208m";
    public static final String ANSI_LIGHT_BLUE = "\u001B[94m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_LIGHT_GREEN = "\u001B[92m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    private static final String[] TAGS = {
        "INFO", "DEBUG", "ERROR", "DEBUGGEE", "INPUT", "EVENT", "CALLSTACK"
    };

    public void print(String text, OutputType outputType) {
        switch (outputType) {
            case INFO -> printTag(TAGS[0], ANSI_LIGHT_BLUE);
            case DEBUG -> printTag(TAGS[1], ANSI_LIGHT_GREEN);
            case ERROR -> printTag(TAGS[2], ANSI_RED);
            case DEBUGGEE -> printTag(TAGS[3], ANSI_ORANGE);
            case USER_INPUT -> printTag(TAGS[4], ANSI_YELLOW);
            case EVENT -> printTag(TAGS[5], ANSI_BLUE);
            case CALLSTACK -> printTag(TAGS[6], ANSI_PURPLE);
        }
        System.out.println(" " + text);
    }

    public void printInfo(String text) {
        print(text, OutputType.INFO);
    }

    public void printDebug(String text) {
        print(text, OutputType.DEBUG);
    }

    public void printError(String text) {
        print(text, OutputType.ERROR);
    }

    public void printDebuggee(String text) {
        print(text, OutputType.DEBUGGEE);
    }

    public void printUserInput(String text) {
        print(text, OutputType.USER_INPUT);
    }

    public void printEvent(String text) {
        print(text, OutputType.EVENT);
    }

    public void printCallStack(String text) {
        print(text, OutputType.CALLSTACK);
    }

    public String colourString(String text, String colour) {
        return colour + text + ANSI_RESET;
    }

    private void printTag(String tag, String color) {
        System.out.printf("[%s%s%s]", color, tag, ANSI_RESET);
        // pad each tag to at least 5 characters
        int padding = 5 - tag.length();
        if (padding > 0) {
            System.out.print(" ".repeat(padding));
        }
    }
}
