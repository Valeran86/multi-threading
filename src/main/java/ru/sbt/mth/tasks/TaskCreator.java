package ru.sbt.mth.tasks;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskCreator implements Runnable {
    private ConcurrentLinkedQueue<Runnable> tasks;

    public TaskCreator( ConcurrentLinkedQueue<Runnable> tasks ) {
        this.tasks = tasks;
    }

    @Override
    public void run( ) {
        while ( !Thread.currentThread().isInterrupted() ) {
            try {
                tasks.add( new Task() );
                System.out.println( "Creator created new task! Total: " + tasks.size() );
                Thread.sleep( 500 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
