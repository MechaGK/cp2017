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
public class Exercise16 {
    /*
    - Modify Exercise 15 such that:
        * Each element in the list of numbers to sum is between 1 and 10.
        * The list of random numbers has random size between 1 and 100.
            (Hint: use Random and the % operator).

    Note: if you use BlockingQueue, you get a head start for Exercise 17.
    */
    public static class BlockingQueue {
        private static class Product {
            private final int[] numbers;

            public Product(int[] numbers) {
                this.numbers = numbers;
            }

            public Product() {
                Random random = new Random();
                this.numbers = new int[Math.abs(random.nextInt() % 100) + 1];

                for (int i = 0; i < this.numbers.length; i++) {
                    this.numbers[i] = Math.abs(random.nextInt() % 10) + 1;
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
                if (latch.getCount() == 0 && list.peek() == null) {
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
