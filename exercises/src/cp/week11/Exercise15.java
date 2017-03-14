package cp.week11;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

/**
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise15 {
    /*
    - Modify producers_consumers (any version of it) such that:
      * A product is a list of 10 random numbers that the consumer
        has to sum together. (Hint: use Random)
    - Apply the same change to the other versions of producers_consumers.
        Is it hard to re-apply this change? Why?
    */
    public static class BlockingQueue {
        private static class Product {
            private final int[] numbers;

            public Product(int[] numbers) {
                this.numbers = numbers;
            }

            public Product() {
                this.numbers = new int[10];

                Random random = new Random();

                for (int i = 0; i < 10; i++) {
                    this.numbers[i] = random.nextInt();
                }
            }

            public String toString() {
                return Arrays.toString(numbers);
            }
        }

        private static final BlockingDeque<Product> THE_LIST = new LinkedBlockingDeque<>();

        private static void produce(BlockingDeque<Product> list, String threadName) {
            IntStream.range(1, 200).forEach(i -> {
                Product prod = new Product();
                list.add(prod);
                System.out.println(threadName + " producing " + prod);
            });
        }

        private static void consume(BlockingDeque<Product> list, String threadName, CountDownLatch latch) {
            boolean keepRun = true;
            while (keepRun) {
                try {
                    Product prod = list.takeFirst();
                    int sum = 0;

                    for (int i = 0; i < prod.numbers.length; i++) {
                        sum += prod.numbers[i];
                    }

                    System.out.println(sum);
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
            IntStream.range(0, NUM_PRODUCERS).forEach(
                    i -> {
                        new Thread(() -> {
                            produce(THE_LIST, "Producer" + i);
                            latch.countDown();
                        }).start();
                        new Thread(() -> {
                            consume(THE_LIST, "Consumer" + i, latch);
                        }).start();
                    });

            // How do we stop the Consumer threads here as in GuardedBlocks?
        }
    }
}
