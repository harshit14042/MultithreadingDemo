package com.example.code;
import java.lang.Thread;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import static java.lang.Thread.yield;

class Container{
    public int[] box;
    public int count;
    public int size;
    public Container(int size){
        this.box=new int[size];
        this.count=0;
        this.size=size;
    }
    public void produce() throws InterruptedException {
        while(true){
            synchronized (this) {
                while(this.count==this.size) {
                    wait();
                }
                int number = (int) (Math.random()*100);
                System.out.printf("%s Produced %d\n",Thread.currentThread().getName(), number);
                this.box[this.count++] = number;
                notifyAll();
                Thread.sleep(500);
            }
        }
    }
    public void consume() throws InterruptedException {
        while(true){
            synchronized (this) {
                while(this.count==0) {
                    wait();
                }
                System.out.printf("%s Consumer %d\n",Thread.currentThread().getName(), this.box[--this.count]);
                notifyAll();
                if(this.box[this.count]>90){
                    Thread.currentThread().stop();
                }
                else {
                    Thread.sleep(500);
                }
            }
        }
    }
}
class ThreadProducer extends Thread{
    public Container c;
    ThreadProducer(Container c){
        this.c=c;
    }
    public void run() {
        try {
            this.c.produce();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class ThreadConsumer extends Thread{
    public Container c;
    ThreadConsumer(Container c){
        this.c=c;
    }
    public void run() {
        try {
            this.c.consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ThreadProducerRunnable implements Runnable{
    public Container c;
    ThreadProducerRunnable(Container c){
        this.c=c;
    }
    public void run() {
        try {
            this.c.produce();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class ThreadConsumerRunnable implements Runnable{
    public Container c;
    ThreadConsumerRunnable(Container c){
        this.c=c;
    }
    public void run() {
        try {
            this.c.consume();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class Main {
    public static void main(String args[]) throws InterruptedException {
        Container c=new Container(5);

        // Execute One at a time

        //Using functional interface Runnable
        Thread t1=new Thread(() -> {
            try {
                c.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2=new Thread(()->{
            try {
                c.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Using Thread class
        ThreadProducer p=new ThreadProducer(c);
        ThreadConsumer co=new ThreadConsumer(c);
        p.start();
        co.start();
        p.join();
        co.join();

        // Using Runnable
        Thread t11=new Thread(new ThreadProducerRunnable(c));
        Thread t21=new Thread(new ThreadConsumerRunnable(c));
        t11.start();
        t21.start();
        t11.join();
        t21.join();

        //Using threadpools
        Runnable p1=new ThreadProducerRunnable(c);
        Runnable p2=new ThreadProducerRunnable(c);
        Runnable c1=new ThreadConsumerRunnable(c);
        Runnable c2=new ThreadConsumerRunnable(c);
        ExecutorService pool=Executors.newFixedThreadPool(3);
        pool.execute(p1);
        pool.execute(p2);
        pool.execute(c1);
        pool.execute(c2);
        pool.shutdown();
    }
}
