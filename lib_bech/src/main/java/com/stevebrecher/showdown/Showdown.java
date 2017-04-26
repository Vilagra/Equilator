package com.stevebrecher.showdown;

import java.io.*;

import static java.lang.System.*;

import java.util.*;

public final class Showdown {

    static int threads = 8;


    public static double[] calculate(String cards,String bord,String[] ranges,CalculatingInProgressListener listener) {
        Enumerator[] enumerators = new Enumerator[threads];
        System.out.println();
        UserInput ui = UserInput.newUserInput(cards, bord,ranges,0);
        long nanosecs = System.currentTimeMillis();
        for (int i = 0; i < enumerators.length; i++) {
            enumerators[i] = new Enumerator(i, threads,
                    ui.deck(), ui.holeCards(),ui.getRange(), ui.boardCards(),listener);
            enumerators[i].start();
        }
        for (Enumerator enumerator : enumerators) {
            try {
                enumerator.join();
            } catch (InterruptedException never) {
            }
        }
        nanosecs = System.currentTimeMillis() - nanosecs;
        System.out.println("sec"+nanosecs);
        return Output.result(ui, enumerators);
    }


}
 