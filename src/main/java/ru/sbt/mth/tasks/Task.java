package ru.sbt.mth.tasks;

public class Task implements Runnable {
    @Override
    public void run( ) {
        try {
            System.out.println( "\tTASK executed by " + Thread.currentThread().getName() );
            Thread.sleep( 1000 );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
