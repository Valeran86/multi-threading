package ru.sbt.mth;

public class TestScalableThreadPool {
    private static final int CALC_COUNT = 1000;

    public static void main( String... args ) {
        ThreadPool threadPool = new ScalableThreadPool(1,8);
        threadPool.start();

        int cores = 4*60;
        System.out.println( "Cores * 4: " + cores );
        for ( int i = 0; i < cores; i++ ) {
            final int numberTask = i;
            threadPool.execute(()->heavyCalc(numberTask));
        }

        try{
            Thread.sleep(10000);
        }catch (Exception ex){
            System.err.println(ex.getMessage());
        }
        System.out.println("New tasks");
        for ( int i = 0; i < 100; i++ ) {
            final int numberTask = i;
            threadPool.execute(()->heavyCalc(numberTask));
        }

        threadPool.shutdown();
    }

    private static void heavyCalc (int numberTask) {
        double a = 0;
        for ( int k = 0; k < CALC_COUNT; k++ ) {
            a += Math.tan( k );
        }

        System.out.println( "task: " + numberTask + " a=" + a +
                " from thread:" + Thread.currentThread().getName() );
    }
}
