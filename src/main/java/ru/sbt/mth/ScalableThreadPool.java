package ru.sbt.mth;

import java.util.ArrayList;
import java.util.LinkedList;

public class ScalableThreadPool implements ThreadPool {
    private final int minCountThread;
    private final int maxCountThread;
    private final ArrayList<ThreadWorker> threads;
    private LinkedList<Runnable> tasks;
    private final Object tasksLock = new Object();
    private boolean isFinished=false;


    ScalableThreadPool(int minCountThread, int maxCountThread) {
        this.minCountThread = minCountThread;
        this.maxCountThread = maxCountThread;

        threads = new ArrayList<>(maxCountThread);
        tasks = new LinkedList<>();

        for (int i = 0; i < minCountThread; i++) {
            ThreadWorker threadWorker=new ThreadWorker();
            threads.add(threadWorker);
            System.out.println("New thread:" + threadWorker.getName());
        }
    }


    @Override
    public void start() {
        for (ThreadWorker thread : threads){
            thread.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasksLock) {
            tasks.add(runnable);
            modifyThreadList();
            tasksLock.notify();
        }
    }

    /** Мнтод изменения перечня потоков
     * @return Возвращает было ли удаление потока в ходе изменения перечня
     */
    private boolean modifyThreadList(){
        boolean isRemove=false;
        if(tasks.size()>threads.size() && threads.size()<maxCountThread){
            ThreadWorker threadWorker=new ThreadWorker();
            threads.add(threadWorker);
            threadWorker.start();
            System.out.println("New thread:" + threadWorker.getName());
        }
        if(tasks.size()<threads.size() && threads.size()>minCountThread){
            int indexThreadRemove=threads.size() - 1;
            System.out.println("Remove thread:" + threads.get(indexThreadRemove).getName());
            threads.get(indexThreadRemove).interrupt();
            threads.remove(indexThreadRemove);
            isRemove=true;
        }
        System.out.println("Count threads: " +
                threads.size()+". Count tasks:" +
                tasks.size()
        );
        return isRemove;
    }


    @Override
    public void shutdown() {
        while ( tasks.size() > 0 ) {
            Thread.yield();
        }

        isFinished=true;

        synchronized (tasksLock) {
            tasksLock.notifyAll();
            threads.forEach(t->System.out.println("Remove thread:"+t.getName()));
            threads.forEach(Thread::interrupt);
        }
    }

    private class ThreadWorker extends Thread {
        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {
                Runnable runnable;
                synchronized (tasksLock) {
                    while (tasks.isEmpty()) {
                        try {
                            if(modifyThreadList() ||(tasks.size()==0 && isFinished)){
                                break;
                            }
                            tasksLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace( System.out);
                            System.err.println("Remove thread:" + Thread.currentThread().getName());
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    if(tasks.size()>0) {
                        runnable = tasks.remove();
                    }else{
                        runnable=null;
                    }
                }
                if(runnable!=null)
                    runnable.run();
            }
        }
    }
}
