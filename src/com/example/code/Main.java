package com.example.code;
import java.lang.Thread;
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
                System.out.printf("Produced %d\n", number);
                this.box[this.count++] = number;
                notify();
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
                System.out.printf("Consumer %d\n", this.box[--this.count]);
                notify();
                Thread.sleep(500);
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
//        Thread t1=new Thread(() -> {
//            try {
//                c.produce();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        Thread t2=new Thread(()->{
//            try {
//                c.consume();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        t1.start();
//        t2.start();
//        t1.join();
//        t2.join();

//        ThreadProducer p=new ThreadProducer(c);
//        ThreadConsumer co=new ThreadConsumer(c);
//        p.start();
//        co.start();
//        p.join();
//        co.join();
        Thread t1=new Thread(new ThreadProducerRunnable(c));
        Thread t2=new Thread(new ThreadConsumerRunnable(c));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
