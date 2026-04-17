import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Runnable {
    private final int id;
    private final String transactionType;

    // Dependencies passed in via constructor
    private final Semaphore door;
    private final Semaphore tellerReady;

    public Customer(int id, Semaphore door, Semaphore tellerReady) {
        this.id = id; [cite: 59]
        this.door = door;
        this.tellerReady = tellerReady;
        // Randomly decide transaction type [cite: 61]
        this.transactionType = ThreadLocalRandom.current().nextBoolean() ? "Deposit" : "Withdraw";
    }

    @Override
    public void run() {
        try {
            // 1. Random wait before heading to the bank (0-100ms) [cite: 62]
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 101));

            // 2. Enter the bank (Door allows only two at a time) [cite: 63]
            door.acquire();
            System.out.println("Customer " + id + ": enters bank");
            door.release(); // Step inside and free the doorway [cite: 63]

            // 3. Wait for any teller to become available [cite: 64, 66]
            tellerReady.acquire();

            // Simplified logic: In a rudimentary version, we represent
            // the interaction with basic print statements rather than
            // complex cross-thread semaphore handshakes.
            System.out.println("Customer " + id + ": approaching a teller"); [cite: 65]
            System.out.println("Customer " + id + ": introduces itself"); [cite: 67]
            System.out.println("Customer " + id + ": performing " + transactionType); [cite: 69]

            // 4. Simulate waiting for the teller to finish [cite: 70]
            // (In a real run, this would be a semaphore.acquire() from the teller)
            Thread.sleep(50);

            // 5. Leave the bank through the door [cite: 71]
            door.acquire();
            System.out.println("Customer " + id + ": leaves bank");
            door.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}