import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class BankSimulation {
    public static Semaphore door = new Semaphore(2);
    public static Semaphore safe = new Semaphore(2);
    public static Semaphore manager = new Semaphore(1);
    public static Semaphore tellerReady = new Semaphore(0);
    public static Semaphore bankOpen = new Semaphore(0);
    public static AtomicInteger totalServed = new AtomicInteger(0);
    public static Teller[] tellers = new Teller[3];
    public static Thread[] tellerThreads = new Thread[3];

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            tellers[i] = new Teller(i);
            tellerThreads[i] = new Thread(tellers[i]);
            tellerThreads[i].start();
        }

        try {
            for (int i = 0; i < 3; i++) bankOpen.acquire();
        } catch (InterruptedException e) {}

        System.out.println("Bank is now open");

        for (int i = 0; i < 50; i++) {
            new Thread(new Customer(i)).start();
        }

        while (totalServed.get() < 50) {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }

        for (Thread t : tellerThreads) {
            t.interrupt();
        }
    }

    public static synchronized Teller findAvailableTeller() {
        for (Teller t : tellers) {
            if (t.isWaitingForCustomer) {
                t.isWaitingForCustomer = false;
                return t;
            }
        }
        return null;
    }
}