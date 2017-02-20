package cp.week6;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise1Alt
{
	/*
	- Create a Counter class storing an integer (a field called i), with an increment and decrement method.
	- Make Counter Thread-safe.
	- Does it make a different to declare i private or public?

	Making i public would give us no guarrentee for any safety as a i could be changed from anywhere
	*/

	public static class Counter {
	    private AtomicInteger i;

	    public Counter(int i) {
	        this.i.set(i);
        }

	    public int increment() {
            return i.incrementAndGet();
        }

        public int decrement() {
	        return i.decrementAndGet();
        }

        public int getValue() {
	        return i.get();
        }
    }
}
