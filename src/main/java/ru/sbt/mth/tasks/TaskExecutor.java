package ru.sbt.mth.tasks;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskExecutor implements Runnable {
    private int taskExecuted = 0;
    private ConcurrentLinkedQueue<Runnable> tasks;

    public TaskExecutor( ConcurrentLinkedQueue<Runnable> tasks ) {
        this.tasks = tasks;
    }

    @Override
    public void run( ) {
        print( "Started..." );
        Runnable task;
        while ( !Thread.currentThread().isInterrupted() ) {
            try {
                task = tasks.poll();
                if ( task != null ) {
                    print( "run task..." );
                    task.run();
                    taskExecuted++;
                } else {
                    print( "tasks not exists. total executed (" + taskExecuted + "). now sleep..." );
                    Thread.sleep( 4000 );
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    private void print( String message ) {
        System.out.println( "Executor " + Thread.currentThread().getName() + ": " + message );
    }
}
