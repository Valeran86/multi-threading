package ru.sbt.mth.tasks;

import ru.sbt.mth.ScalableThreadPool;

import java.util.Queue;

public class TaskCreator implements Runnable {
    private final Queue< Runnable > tasks;
    private final ScalableThreadPool threadPool;

    public TaskCreator( Queue< Runnable > tasks ) {
        this.tasks = tasks;
        this.threadPool = null;
        putXTasks( 10 );

    }

    public TaskCreator( Queue< Runnable > tasks, ScalableThreadPool threadPool ) {
        this.tasks = tasks;
        this.threadPool = threadPool;
        putXTasks( 10 );
    }

    private void putXTasks( int x ) {
        for ( int i = 0; i < x; i++ ) {
            tasks.add( new Task() );
        }
    }

    @Override
    public void run() {
        while ( !Thread.currentThread().isInterrupted() ) {
            synchronized ( tasks ) {
                tasks.add( new Task() );
                System.out.println( "Creator created new task! Total: " + tasks.size() );

                if ( threadPool != null ) {
                    threadPool.printThreadsInfo();
                    if ( threadPool.isAllThreadsIsRunnable() && threadPool.getCurrentThreadCount() < threadPool.getMax() )
                        threadPool.poolInc();
                    else if ( threadPool.isExistsWaitingThreads() && threadPool.getCurrentThreadCount() > threadPool.getMin() )
                        threadPool.poolDec();
                }
                tasks.notifyAll();
            }
            try {
                Thread.sleep( 100 );
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
