package cp.week6;

import cp.week6.Exercise1.Counter;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise5
{
	/*
	- Apply the technique for fixing Listing 4.14 to Listing 4.15 in the book, but to the following:
	- Create a thread-safe Counter class that stores an int and supports increment and decrement.
	- Create a new thread-safe class Point, which stores two Counter objects.
	- The two counter objects should be public.
	- Implement the method boolean areEqual() in Point, which returns true if the two counters store the same value.
	*/

	public static class Point {
	    public Counter x;
	    public Counter y;

	    public synchronized boolean areEqual() {
	        return x.getValue() == y.getValue();
        }
    }
}
