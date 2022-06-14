package dump.thr2;
public class Std01Thread {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        Thread1 t1 = new Thread1(lock);
        Thread2 t2 = new Thread2(lock);
        Thread3 t3 = new Thread3(lock);
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }
}
