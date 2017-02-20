package cp.week7;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
@SuppressWarnings("ALL")
public class Exercise9
{
	/*
	- Modify producer_consumer/BusyWait such that each producer randomly decides
	  the number of elements that it will puts in the list.
	- Hint: use java.util.Random. Documentation: https://docs.oracle.com/javase/7/docs/api/java/util/Random.html
	- Make it so the number of elements produced by each producer cannot exceed 10000.
	*/

    public static class BusyWait
    {
        final static Random random = new Random();

        private static class Product {
            private final String name;
            private final String attributes;
            public Product( String name, String attributes )
            {
                this.name = name;
                this.attributes = attributes;
            }

            public String toString()
            {
                return name + ". " + attributes;
            }
        }
        private static final Deque< Product > THE_LIST = new LinkedList<>();

        private static void produce( Deque< Product > list, String threadName )
        {
            IntStream.range( 1, random.nextInt(10001) ).forEach(i -> {
                if ( i % 2 == 0) {
                    synchronized( list ) {
                        Product prod = new Product( "Water Bottle", "Liters: " + i + ". By thread: " + threadName );
                        list.add( prod );
                        System.out.println( threadName + " producing " + prod );
                    }
                }
                else {
                    synchronized( list ) {
                        Product prod = new Product( "Flower Bouquet", "Amount: " + i + ". By thread: " + threadName );
                        list.add( prod );
                        System.out.println( threadName + " producing " + prod );
                    }
                }
            } );
        }

        private static void consume( Deque< Product > list, String threadName, CountDownLatch latch )
        {
            boolean keepRun = true;
            while( keepRun ) {
                synchronized( list ) {
                    if ( !list.isEmpty() ) {
                        Product prod = list.removeFirst();
                        System.out.println( threadName + " consuming " + prod.toString() );
                    } else if ( latch.getCount() == 0 ) {
                        keepRun = false;
                    }
                }
            }
        }

        private static final int NUM_PRODUCERS = 3;

        public static void run()
        {
            CountDownLatch latch = new CountDownLatch( NUM_PRODUCERS );
            IntStream.range( 0, NUM_PRODUCERS ).forEach(
                    i -> {
                        new Thread( () -> {
                            produce( THE_LIST, "Producer" + i );
                            latch.countDown();
                        } ).start();
                        new Thread( () -> {
                            consume( THE_LIST, "Consumer" + i, latch );
                        } ).start();
                    } );

		/*
		IntStream.range( 0, NUM_PRODUCERS ).forEach( i -> { ... } );

		does the same thing as

		for( int i = 0; i < NUM_PRODUCERS; i++ ) {
			...
		}
		*/
        }
    }
}
