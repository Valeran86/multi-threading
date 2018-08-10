package ru.sbt.mth;

import ru.sbt.mth.tasks.TaskCreator;
import ru.sbt.mth.tasks.TaskExecutor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.IntStream;

/***
 * FixedThreadPool - Количество потоков задается в конструкторе и не меняется.
 */
public class FixedThreadPool implements ThreadPool {
    private final int threadCount;
    private final Queue< Runnable > tasks = new LinkedList<>();

    public FixedThreadPool( int threadCount ) {
        this.threadCount = threadCount;
    }

    @Override
    public void start() {
        IntStream.range( 0, threadCount )
                .mapToObj( i -> new Thread( new TaskExecutor( tasks ) ) )
                .forEach( Thread::start );

        try {
            Thread.sleep( 5000 );
            System.out.println( "Creator started!" );
            new Thread( new TaskCreator( tasks ) ).start();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute( Runnable runnable ) {
        new Thread( runnable ).start();
    }
}