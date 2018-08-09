package ru.sbt.mth;

public class   Test {
    static final int CALC_COUNT = 1000000;

    public static void main( String... args ) {
        ThreadPool threadPool = new ScalableThreadPool();
        threadPool.start();

        int cores = 4 * Runtime.getRuntime().availableProcessors();
        System.out.println( "Cores * 4: " + cores );
        for ( int i = 0; i < cores; i++ ) {
            final int numberTask = i;
            threadPool.execute( () -> heavyCalc( numberTask ) );
        }

        threadPool.shutdown();
    }

    public static void heavyCalc( int numberTask ) {
        double a = 0;
        for ( int k = 0; k < CALC_COUNT; k++ ) {
            a += Math.tan( k );
        }

        System.out.println( "task: " + numberTask + " a=" + a +
                " from thread:" + Thread.currentThread().getName() );


    }
}
