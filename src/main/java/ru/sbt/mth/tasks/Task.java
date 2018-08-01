package ru.sbt.mth.tasks;

public class Task implements Runnable {
    @Override
    public void run() {
        System.out.println( "\tTASK executed by " + Thread.currentThread().getName() );
        double a = 0;
        for ( int k = 0; k < 1000000; k++ ) {
            a += Math.tan( k );
        }
    }
}
