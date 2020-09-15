package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
/**
 * An Event sent by the {@link LogisticsService} Service to {@link ResourcesHolder} Service
 * in order to process the Delivery Event
 */

public class DeliveryEvent implements Event {

    private String address;
    private int distance;

    public DeliveryEvent(String address, int distance){
        this.address = address;
        this.distance = distance;
    }
    /**
     * Retrieves the Distance of the customer address from the store.
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Retrieves the Adress of the customer.
     */
    public String getAddress() {
        return address;
    }


}
