package ru.sbt.mth;


import ru.sbt.mth.tasks.TaskCreator;
import ru.sbt.mth.tasks.TaskExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.WAITING;

/***
 * ScalableThreadPool в конструкторе задается минимальное и максимальное(int min, int max) число потоков,
 * количество запущенных потоков может быть увеличено от минимального к максимальному,
 * если при добавлении нового задания в очередь нет свободного потока для исполнения этого задания.
 * При отсутствии задания в очереди, количество потоков опять должно быть уменьшено до значения min
 */
public class ScalableThreadPool implements ThreadPool {
    private final int min;
    private final int max;
    private final List<Thread> pool;
    private final Queue<Runnable> tasks;

    public ScalableThreadPool( int min, int max ) {
        this.min = min;
        this.max = max;
        pool = new CopyOnWriteArrayList<>();
        tasks = new ConcurrentLinkedQueue<>();
    }

    public int getMin( ) {
        return min;
    }

    public int getMax( ) {
        return max;
    }

    public int getCurrentThreadCount( ) {
        return pool.size();
    }

    @Override
    public void start( ) {
        pool.addAll( IntStream.range( 0, min )
                .mapToObj( i -> new Thread( new TaskExecutor( tasks ) ) )
                .peek( Thread::start )
                .collect( Collectors.toList() ) );

        try {
            Thread.sleep( 5000 );
            System.out.println( "Creator started!" );
            new Thread( new TaskCreator( tasks, this ) ).start();
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public boolean isAllThreadsIsRunnable( ) {
        return this.pool.stream().allMatch( t -> t.getState().equals( BLOCKED ) );
    }

    public boolean isExistsWaitingThreads( ) {
        return this.pool.stream().anyMatch( t -> t.getState().equals( WAITING ) );
    }


    @Override
    public void execute( Runnable runnable ) {
        runnable.run();
    }

    public void poolInc( ) {
        System.out.println( "+++++++++++++++++++++++++increment" );

        if ( pool.size() < max ) {
            Thread newThread = new Thread( new TaskExecutor( tasks ) );
            newThread.start();
            pool.add( newThread );
        }
    }

    public void poolDec( ) {
        System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!decrement" );
        if ( pool.size() > min ) {
            Optional<Thread> opt = this.pool.stream().filter( t -> t.getState().equals( WAITING ) ).findAny();
            Thread t = opt.orElseGet( ( ) -> this.pool.get( getCurrentThreadCount() - 1 ) );
            pool.remove( t );
            t.interrupt();
        }
    }

    public void printThreadsInfo( ) {
        System.out.println( "--------------" );
        pool.forEach( t -> System.out.print( t.getName() + ":" + t.getState() + " " ) );
        System.out.println( " - " + min + "," + max + "," + pool.size() );
        System.out.println( "--------------" );
    }
}
