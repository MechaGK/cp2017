package cp.week7;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise7
{
	/*
	- Modify producer_consumer/Sequential to use for-loops instead of IntStream::forEach
	*/

    public static class Sequential
    {
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

        private static void produce( Deque< Product > list )
        {
            for(int i = 1; i <= 1000; i++) {
                list.add( new Product( "Water Bottle", "Liters: " + i ) );
                list.add( new Product( "Flower Bouquet", "Amount: " + i ) );
            }
        }

        private static void consume( Deque< Product > list )
        {
            while( !list.isEmpty() ) {
                Product prod = list.removeFirst();
                System.out.println( prod ); // Equivalent to System.out.println( prod.toString() );
            }
        }

        public static void run()
        {
            produce( THE_LIST );
            consume( THE_LIST );
        }
    }
}
