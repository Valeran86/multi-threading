package ru.sbt.mth.tasks;

import java.util.Queue;

public class TaskExecutor implements Runnable {
    private int taskExecuted = 0;
    private final Queue< Runnable > tasks;

    public TaskExecutor( Queue< Runnable > tasks ) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        print( "Started..." );
        Runnable task;
        while ( !Thread.currentThread().isInterrupted() ) {
            synchronized ( tasks ) {
                task = tasks.poll();
                if ( task != null ) {
                    print( "run task..." );
                    task.run();
                    taskExecuted++;
                } else {
                    try {
                        print( "tasks not exists. total executed (" + taskExecuted + "). waiting..." );
                        tasks.wait();
                    } catch ( InterruptedException e ) {
                        break;
                    }
                }
            }
        }
        print( "INTERRUPTED! break thread!" );
    }

    private void print( String message ) {
        System.out.println( "Executor " + Thread.currentThread().getName() + ": " + message );
    }
}
