package com.stevebrecher.showdown;

final class Showdown {

    private static final int threads = 8;


    public static double[] calculate(String cards, String bord, String[] ranges, CalculatingInProgressListener listener, int trail) {
        Enumerator[] enumerators = new Enumerator[threads];
        UserInput ui = UserInput.newUserInput(cards, bord,ranges);
        long nanosecs = System.currentTimeMillis();
        for (int i = 0; i < enumerators.length; i++) {
            enumerators[i] = new Enumerator(i, threads,
                    ui.deck(), ui.holeCards(),ui.getRange(), ui.boardCards(),listener,trail);
            enumerators[i].start();
        }
        for (Enumerator enumerator : enumerators) {
            //noinspection EmptyCatchBlock
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
 