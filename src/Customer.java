import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Runnable {
    private int id;
    private String type;

    public Customer(int id) {
        this.id = id;
        this.type = ThreadLocalRandom.current().nextBoolean() ? "Withdraw" : "Deposit";
    }

    public void run() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 101));

            BankSimulation.door.acquire();
            System.out.println("Customer " + id + ": enters bank");
            BankSimulation.door.release();

            BankSimulation.tellerReady.acquire();
            Teller myTeller = BankSimulation.findAvailableTeller();

            System.out.println("Customer " + id + " [Teller " + myTeller.id + "]: introduces itself");
            myTeller.currentCustId = id;
            myTeller.currentTrans = type;
            myTeller.customerArrived.release();

            Thread.sleep(10);
            System.out.println("Customer " + id + " [Teller " + myTeller.id + "]: " + type);
            myTeller.transactionRequest.release();

            myTeller.tellerDone.acquire();

            BankSimulation.door.acquire();
            System.out.println("Customer " + id + ": leaves bank");
            BankSimulation.door.release();
            myTeller.customerLeft.release();

        } catch (InterruptedException e) {}
    }
}