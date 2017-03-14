package cp.week11;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise17 {
    /*
    ! (Exercises marked with ! are challenging, but they will make you feel
       good when you succeed and are excellent preparation for the exam.
       Take a deep breath before starting.)

    - Modify producers_consumers/BlockingQueue such that:
        * products are lists of numbers to sum up as in Exercise 16.
            (If you used blocking queues in Exercise 16, you can start from there.)
        * There are two queues of products: THE_LIST and THE_OTHER_LIST.
        * When a consumer on THE_LIST consumes a product (and sums up the numbers),
          it stores the summed up totals in a local list (called totals).
        * When the local list of totals of a consumer on THE_LIST
          reaches size 4, the consumer puts the list of totals as a product
          in THE_OTHER_LIST and then empties its own local list.

          (Beware: you need to be careful of not using the same list
          locally in the consumer and in the element that you put in
          THE_OTHER_LIST, or you risk emptying both when you reset it
          in the consumer.)
        * There are a few consumers (of another kind) waiting on THE_OTHER_LIST.
          When one of these consumers gets a product from THE_OTHER_LIST,
          it sums up the numbers contained within and prints the result on
          screen.
    */
    public static class BlockingQueue {
        final static AtomicInteger finishedProducers = new AtomicInteger(1);
        final static AtomicInteger finishedConsumers = new AtomicInteger(1);

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
        private static final BlockingDeque<Product> THE_OTHER_LIST = new LinkedBlockingDeque<>();

        private static void produce(BlockingDeque<Product> list, String threadName) {
            IntStream.range(1, 200).forEach(i -> {
                Product prod = new Product();
                list.add(prod);
                System.out.println(threadName + " producing " + prod);
            });

            finishedProducers.getAndIncrement();
        }

        private static void consume(BlockingDeque<Product> list, String threadName, CountDownLatch latch) {
            int[] localList = new int[4];
            int localListIndex = 0;

            while (true) {
                if (finishedProducers.get() >= NUM_PRODUCERS && list.peek() == null) {
                    break;
                }

                try {
                    Product prod = list.takeFirst();
                    int sum = 0;

                    for (int i : prod.numbers) {
                        sum += i;
                    }

                    localList[localListIndex] = sum;
                    localListIndex++;

                    if (localListIndex >= localList.length) {
                        THE_OTHER_LIST.add(new Product(localList));

                        localList = new int[localList.length];
                        localListIndex = 0;
                    }
                } catch (InterruptedException e) {
                }
            }

            finishedConsumers.getAndIncrement();
        }

        private static void consumeOther(BlockingDeque<Product> list, String threadName, CountDownLatch latch) {
            while (true) {
                if (finishedConsumers.get() >= NUM_PRODUCERS && list.peek() == null) {
                    break;
                }

                try {
                    Product prod = list.takeFirst();
                    int sum = 0;

                    for (int n : prod.numbers) {
                        sum += n;
                    }

                    System.out.println(sum);

                } catch (InterruptedException e) {
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
                        new Thread(() -> {
                            consumeOther(THE_OTHER_LIST, "Other Consumer" + i, latch);
                        }).start();
                    });

            // How do we stop the Consumer threads here as in GuardedBlocks?
        }
    }
}
