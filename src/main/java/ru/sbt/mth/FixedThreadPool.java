package ru.sbt.mth;

import ru.sbt.mth.ru.sbt.mth.tasks.TaskCreator;
import ru.sbt.mth.ru.sbt.mth.tasks.TaskExecutor;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

/***
 * FixedThreadPool - Количество потоков задается в конструкторе и не меняется.
 */
public class FixedThreadPool implements ThreadPool {
    private final int threadCount;
    private ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public FixedThreadPool( int threadCount ) {
        this.threadCount = threadCount;
    }

    @Override
    public void start( ) {
        IntStream.range( 0, threadCount )
                .mapToObj( i -> new Thread( new TaskExecutor( tasks ) ) )
//                .peek( i -> System.out.println( "Executor " + i.getName() + " started!" ) )
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
        runnable.run();
    }
}