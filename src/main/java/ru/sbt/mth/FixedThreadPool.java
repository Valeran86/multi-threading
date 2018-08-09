package ru.sbt.mth;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FixedThreadPool implements ThreadPool {
    private final static int THREAD_COUNT = 3;

    private final Thread[] workers = new Thread[THREAD_COUNT];
    private List<Runnable> tasks = Collections.synchronizedList(
            new LinkedList<>()
    );

    @Override
    public void start() {
        for ( int i = 0; i < THREAD_COUNT; i++ ) {
            workers[i] = new ThreadWorker();
            workers[i].start();
        }
    }

    @Override
    public void execute( Runnable runnable ) {
        synchronized ( this ) {
            tasks.add( runnable );
            notify();
        }
    }

    @Override
    public void shutdown() {
        while ( tasks.size() > 0 ) {
            Thread.yield();
        }

        for ( int i = 0; i < THREAD_COUNT; i++ )
            workers[i].interrupt();

        synchronized ( this ) {
            notifyAll();
        }
    }

    class ThreadWorker extends Thread {
        @Override
        public void run() {
            while ( !Thread.currentThread().isInterrupted() ) {
                synchronized ( FixedThreadPool.this ) {
                    try {
                        if ( FixedThreadPool.this.tasks.size() == 0 )
                            FixedThreadPool.this.wait();
                    } catch ( InterruptedException e ) {

                        e.printStackTrace( System.out );
                        Thread.currentThread().interrupt();
                    }
                }

                Runnable task;

                synchronized ( FixedThreadPool.this ) {
                    //TODO why it works after shutdown?

                    if (FixedThreadPool.this.tasks.size()>0) {
                        task = FixedThreadPool.this.tasks.get( 0 );
                        FixedThreadPool.this.tasks.remove( task );
                        task.run();
                    }
                }



            //    System.out.println( "Задача завершена" );
            }
        }
    }
}
