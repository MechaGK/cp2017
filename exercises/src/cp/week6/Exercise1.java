package cp.week6;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise1
{
	/*
	- Create a Counter class storing an integer (a field called i), with an increment and decrement method.
	- Make Counter Thread-safe.
	- Does it make a different to declare i private or public?

	Making i public would give us no guarrentee for any safety as a i could be changed from anywhere
	*/

	public static class Counter {
	    private int i;

	    public Counter(int i) {
	        this.i = i;
        }

	    public synchronized int increment() {
            i++;
            return i;
        }

        public synchronized int decrement() {
	        i--;
	        return i;
        }

        public synchronized int getValue() {
	        return i;
        }
    }
}
