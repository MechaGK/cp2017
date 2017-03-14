package cp;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

/**
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class BlockingQueue {
    private final static Random random = new Random();

    private static class Product {
        private final String name;
        private final String attributes;

        public Product(String name, String attributes) {
            this.name = name;
            this.attributes = attributes;
        }

        public String toString() {
            return name + ". " + attributes;
        }
    }

    private static class PoisonPill extends Product {
        public PoisonPill(String name, String attributes) {
            super(name, attributes);
        }
    }

    private static final BlockingDeque<Product> THE_LIST = new LinkedBlockingDeque<>();

    private static void produce(BlockingDeque<Product> list, String threadName) {
        int numberElements = random.nextInt(10001);
        IntStream.range(1, numberElements).forEach(i -> {
            Product prod = new Product("Water Bottle", "Liters: " + i + ". By thread: " + threadName);
            list.add(prod);
            System.out.println(threadName + " producing " + prod);
        });
    }

    @SuppressWarnings("Duplicates")
    private static void consume(BlockingDeque<Product> list, String threadName, CountDownLatch latch) {
        boolean keepRun = true;
        while (keepRun) {
            try {
                Product prod = list.takeFirst();
                System.out.println(threadName + " consuming " + prod.toString());
            } catch (InterruptedException e) {
            }
            if (latch.getCount() == 0) {
                keepRun = false;
            }
        }
    }

    private static final int NUM_PRODUCERS = 3;

    public static void run() {
        // Proposal 1: Before the consumer waits, it checks if something is in the list.
        // Proposal 2: Before the producer sends the signal, it checks if a consumer is waiting.

        CountDownLatch latch = new CountDownLatch(NUM_PRODUCERS);

        for (int i = 0; i < NUM_PRODUCERS; i++) {
            int finalI = i;
            new Thread(() -> {
                produce(THE_LIST, "Producer" + finalI);
                latch.countDown();
            }).start();
            if (i % 2 == 0) {
                new Thread(() -> {
                    consume(THE_LIST, "Consumer" + finalI, latch);
                }).start();
            }
        }

        // How do we stop the Consumer threads here as in GuardedBlocks?
    }
}
