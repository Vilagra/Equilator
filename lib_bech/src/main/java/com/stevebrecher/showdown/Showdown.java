package com.stevebrecher.showdown;

import java.io.*;

import static java.lang.System.*;

import java.util.*;

public final class Showdown {

    static final String VERSION = "2010Jun20.0";

    // The following defaults can be overridden by program arguments
    // -------------------------------------------------------------
    // Argument format:  output path_to_file
    static File outFile = new File("Showdown.txt");    // in addition to console
    // Argument format:  threads number_of_threads
    static int threads = 5;//Runtime.getRuntime().availableProcessors();
    // Argument format: timed
    static boolean timed = false;    // output computation time?
    // Argument format: repeat
    // --useful in exercising the JVM/JIT compiler on the enumeration code path
    // for a specific user input before observing the computation time.  Normally
    // the timed argument would also be provided.
    static boolean repeat = false; // repeat calc on first user input until externally terminated?

    private static PrintStream outStream;

    public static double[] calculate(String cards,String bord) {
        Enumerator[] enumerators = new Enumerator[threads];
        UserInput ui = UserInput.newUserInput(cards, bord);
        long nanosecs = System.nanoTime();
        for (int i = 0; i < enumerators.length; i++) {
            enumerators[i] = new Enumerator(i, threads,
                    ui.deck(), ui.holeCards(), ui.nUnknown(), ui.boardCards());
            enumerators[i].start();
        }
        for (Enumerator enumerator : enumerators) {
            try {
                enumerator.join();
            } catch (InterruptedException never) {
            }
        }
        nanosecs = System.nanoTime() - nanosecs;
        System.out.println(nanosecs);
        //Output.resultsOut(ui,enumerators,outStream);
        return Output.result(ui, enumerators);
    }

    public static void main(String[] args) {
        //System.out.println(Arrays.toString(calculate()));
        //if (getArgs(args)) {

            openOutFile();
            //showIntro();
/*
            Enumerator[] enumerators = new Enumerator[threads];
            UserInput ui = UserInput.newUserInput("AhAcKhKc,JhJc,9c8h,2c3c,7c7d", "");

            while (ui != null) {

                long nanosecs = System.nanoTime();

                for (int i = 0; i < enumerators.length; i++) {
                    enumerators[i] = new Enumerator(i, threads,
                            ui.deck(), ui.holeCards(), ui.nUnknown(), ui.boardCards());
                    enumerators[i].start();
                }
                for (Enumerator enumerator : enumerators) {
                    try {
                        enumerator.join();
                    } catch (InterruptedException never) {
                    }
                }
                Output.result(ui, enumerators);
                nanosecs = System.nanoTime() - nanosecs;*/

/*				if (timed)
                    for (PrintStream stream : new PrintStream[] {System.out, outStream}) {
						stream.printf("seconds = %.2f%n", nanosecs / 1e9);
						stream.flush();
					}*/
    /*            ui = null;
                if (false)
                    ui = UserInput.newUserInput("", "");
            }*/
        //}
        System.out.println(Arrays.toString(calculate("AsKs,JhTh","9h8h8d")));
    }

    private static boolean getArgs(String[] args) {

        int threads = 0;
        File outFile = null;

        Iterator<String> iter = new ArrayList<String>(Arrays.asList(args)).iterator();
        String error = "";

        while (iter.hasNext()) {
            String arg = iter.next().toLowerCase();
            if (arg.equals("threads")) {
                if (iter.hasNext()) {
                    try {
                        threads = Integer.parseInt(iter.next());
                    } catch (NumberFormatException e) {
                    }
                }
                if (threads > 0)
                    Showdown.threads = threads;
                else {
                    error = "Threads value must be a number greater than zero.";
                    break;
                }
            } else if (arg.equals("output")) {
                if (iter.hasNext()) {
                    outFile = new File(iter.next());
                    try {
                        outFile.createNewFile();
                    } catch (Exception e) {
                        outFile = null;
                    }
                }
                if (outFile != null)
                    Showdown.outFile = outFile;
                else {
                    error = "Output value missing or unable to access or create file.";
                    break;
                }
            } else if (arg.equals("timed")) {
                Showdown.timed = true;
            } else if (arg.equals("repeat")) {
                Showdown.repeat = true;
            } else
                error = "Unrecognized program argument: " + arg;
        }
        if (error.length() > 0) {
            out.println(error);
            return false;
        }
        return true;
    }

    private static void openOutFile() {

        try {
            final boolean APPEND = true;
            outStream = new PrintStream(new BufferedOutputStream(
                    new FileOutputStream(outFile, APPEND)));
            if (outFile.length() == 0)
                outStream.println("View this file with a fixed-width font such as Courier");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void showIntro() {
        out.println("        HoldEm Showdown version " + VERSION + " written by Steve Brecher");
        out.println("Deals all possible boards to get exact win probability for each hand specified.");
        out.println("Results written/appended to \"" + outFile + "\".");
        out.println();
        out.println("For general help, type \"help\" or just \"h\" followed by Return or Enter.");
        out.println("For help with a specific response, type \"?\" followed by Return or Enter.");
    }
}
 