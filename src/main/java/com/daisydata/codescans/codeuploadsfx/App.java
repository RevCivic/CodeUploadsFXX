package com.daisydata.codescans.codeuploadsfx;


public class App {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("cli")) {
            System.out.println("Launching in CLI mode...");
            String[] trimmedArgs = java.util.Arrays.copyOfRange(args, 1, args.length);
            ProcessUploadsStandalone.main(trimmedArgs);
        } else {
            System.out.println("Launching GUI...");
            CodeScansApplication.main(args);
        }
    }
}

// TODO: refocus textbox after clicking submit