package ru.sbt.mth;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ScalableThreadPool implements ThreadPool{
    private final static int THREAD_MIN_COUNT = 3;
    private final static int THREAD_MAX_COUNT = 7;
    public volatile static int workersCount;
    private final Thread[] workers = new Thread[THREAD_MAX_COUNT];
    private List<Runnable> tasks = Collections.synchronizedList(
            new LinkedList<>()
    );


    @Override
    public void start( ) {
        for ( int i = 0; i < THREAD_MIN_COUNT; i++ ) {
            workers[i] = new ThreadWorker();
            workers[i].start();

        }
        workersCount=THREAD_MIN_COUNT;
    }

    @Override
    public void execute( Runnable runnable ) {
        synchronized ( this ) {
            tasks.add( runnable );
            notify();
        }
    }

    @Override
    public void shutdown( ) {
        while ( tasks.size() > 0 ) {
            Thread.yield();
        }

        for ( int i = 0; i < workersCount; i++ )
            workers[i].interrupt();

        synchronized ( this ) {
            notifyAll();
        }
    }
    class ThreadWorker extends Thread {
              @Override
              public void run() {
                  while ( !Thread.currentThread().isInterrupted() ) {
                      //Если задач нет, и размер пула не превышает минимальный, то прост ждем
                      synchronized ( ScalableThreadPool.this ) {
                          try {
                              if ( ScalableThreadPool.this.tasks.size() == 0 && workersCount<=THREAD_MIN_COUNT) //==THREAD_MIN_COUNT
                                  ScalableThreadPool.this.wait();
                          } catch ( InterruptedException e ) {
                             System.out.println( "Поток прерван" );

                          }
                      }
                      //Если задач нет, размер пула больше минимального, сокращаем размер

                      synchronized ( ScalableThreadPool.this ) {
                          while ( ScalableThreadPool.this.tasks.size( ) == 0 && workersCount > THREAD_MIN_COUNT ) {
                              workers[ workersCount - 1 ].interrupt( );

                              workersCount--;
                              System.out.println( "Сузили пул потоков до " + workersCount );
                          }

                      }
                      Runnable task;

                      //Если размер пула меньше максимума, и есть задачи увеличиваем размер пула

                      synchronized ( ScalableThreadPool.this ) {
                          while ( ScalableThreadPool.this.tasks.size( ) > 1 && workersCount < THREAD_MAX_COUNT ) {
                              workersCount++;
                              System.out.println( "Расширили пул потоков до " + workersCount );
                              workers[ workersCount-1 ] = new ThreadWorker( );
                              workers[ workersCount-1 ].start( );
                          }
                      }

                      synchronized ( ScalableThreadPool.this ) {
                          //TODO why it works after shutdown?

                          if (ScalableThreadPool.this.tasks.size()>0) {
                              task = ScalableThreadPool.this.tasks.get( 0 );
                              ScalableThreadPool.this.tasks.remove( task );
                              task.run();
                          }
                      }
                          System.out.println( "Задача завершена" );
                  }
              }


    }
}

