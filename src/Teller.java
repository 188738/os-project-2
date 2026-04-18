import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Teller implements Runnable {
    public int id;
    public boolean isWaitingForCustomer = false;
    public Semaphore customerArrived = new Semaphore(0);
    public Semaphore transactionRequest = new Semaphore(0);
    public Semaphore tellerDone = new Semaphore(0);
    public Semaphore customerLeft = new Semaphore(0);
    public int currentCustId;
    public String currentTrans;

    public Teller(int id) { this.id = id; }

    public void run() {
        BankSimulation.bankOpen.release();
        try {
            while (BankSimulation.totalServed.get() < 50) {
                isWaitingForCustomer = true;
                BankSimulation.tellerReady.release();
                customerArrived.acquire();

                System.out.println("Teller " + id + " [Customer " + currentCustId + "]: asks for transaction");
                transactionRequest.acquire();

                if (currentTrans.equals("Withdraw")) {
                    System.out.println("Teller " + id + ": going to manager");
                    BankSimulation.manager.acquire();
                    System.out.println("Teller " + id + ": using manager");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(5, 31));
                    System.out.println("Teller " + id + ": done with manager");
                    BankSimulation.manager.release();
                }

                System.out.println("Teller " + id + ": going to safe");
                //The teller acquires one spot in the semaphore
                BankSimulation.safe.acquire();
                System.out.println("Teller " + id + ": using safe");
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 51));
                System.out.println("Teller " + id + ": done with safe");
                BankSimulation.safe.release();

                System.out.println("Teller " + id + " [Customer " + currentCustId + "]: transaction complete");
                // the teller lets go of one spot from the semaphore
                tellerDone.release();
                customerLeft.acquire();

                BankSimulation.totalServed.incrementAndGet();
            }
        } catch (InterruptedException e) {}
    }
}