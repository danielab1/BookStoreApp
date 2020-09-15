package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

/**
 * An Event sent by the {@link SellingService} in order to check the amount of book in the
 * inventory and return its price if the amoune is greater that 0
 * @return the price of the book if it's amount is greater that 0 or -1 otherwise
 */
public class CheckAvailabilityAndGetPrice implements Event<Integer> {

    private String name;

    public CheckAvailabilityAndGetPrice(String name){
        this.name=name;
    }

    /**
     * Retrieves the name of the book.
     */
    public String getName() {
        return name;
    }

}
