package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * An Event sent by the{@link SellingService} Service to take A book
 */
public class TakeBook implements Event<OrderResult> {
    private String name;

    public TakeBook(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the book to take.
     */
    public String getName() {
        return name;
    }
}

