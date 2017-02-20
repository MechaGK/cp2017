package cp.week6;

import java.util.ArrayList;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class Exercise2
{
	/*
	- Create a static class for cacheing the names of some cities in a static field.
	- Initialise the static field with some cities, e.g., Copenhagen and Odense.
	- Start two threads that each adds some (different) cities.
	- The two threads will share the static field, potentially having problems.
	- Make the static field for city names a ThreadLocal to make it local to threads.
	*/

	public static class CityCacher {
	    public static ThreadLocal<ArrayList<String>> cityList = new ThreadLocal<>();
    }

    public static void main(String[] args) {
	    CityCacher.cityList.set(new ArrayList<>());
	    CityCacher.cityList.get().add("Copenhagen");
	    CityCacher.cityList.get().add("Odense");

	    Thread t1 = new Thread(() -> {
            CityCacher.cityList.get().add("Aalborg");
            CityCacher.cityList.get().add("Aarhus");
            System.out.println(CityCacher.cityList.get());
        });

        Thread t2 = new Thread(() -> {
            CityCacher.cityList.get().add("Nibe");
            CityCacher.cityList.get().add("Vokslev");
            System.out.println(CityCacher.cityList.get());
        });

        t1.run();
        t2.run();
    }
}
