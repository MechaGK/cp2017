package cp.week11;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise14 {
    /*
	- Modify producer_consumer/BlockingQueue such that each consumer is notified
	  of when the program terminated.
	- Hint: Use a special class PoisonPill that extends Product and check if
	  the product taken from the queue in the consumer is a PoisonPill.
	*/

    public static class BlockingQueue {
        final static AtomicInteger finishedProducers = new AtomicInteger(1);

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

        private static final BlockingDeque<Product> THE_LIST = new LinkedBlockingDeque<>();

        private static void produce(BlockingDeque<Product> list, String threadName) {
            IntStream.range(1, 200).forEach(i -> {
                Product prod = new Product("Water Bottle", "Liters: " + i + ". By thread: " + threadName);
                list.add(prod);
                System.out.println(threadName + " producing " + prod);
            });

            finishedProducers.getAndIncrement();
        }

        private static void consume(BlockingDeque<Product> list, String threadName) {
            while (true) {
                if (finishedProducers.get() >= NUM_PRODUCERS && list.peek() == null) {
                    break;
                }

                try {
                    Product prod = list.takeFirst();
                    System.out.println(threadName + " consuming " + prod.toString());
                } catch (InterruptedException e) {
                }
            }
        }

        private static final int NUM_PRODUCERS = 3;

        public static void run() {
            IntStream.range(0, NUM_PRODUCERS).forEach(
                    i -> {
                        new Thread(() -> {
                            produce(THE_LIST, "Producer" + i);
                        }).start();
                        new Thread(() -> {
                            consume(THE_LIST, "Consumer" + i);
                        }).start();
                    });
        }
    }
}
