package ru.sbt.mth;

public class Runner {

    public static void main( String[] args ) {
        ThreadPool tp = new FixedThreadPool( 3 );
        tp.start();
    }

}
