package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
/**
 * An Event sent by the{@link APIService} Service to ask for buy the specified book
 */

public class BookOrderEvent implements Event<OrderReceipt> {
    private String bookName;
    private int tick;
    private Customer customer;

    public BookOrderEvent(Customer c, String bookName, int tick){
        this.bookName = bookName;
        this.tick = tick;
        this.customer = c;
    }
    /**
     * Retrieves the name of the book.
     */
    public String getBookName(){
        return bookName;
    }

    /**
     * Retrieves the Tick that represents the tick when the order was made
     */
    public int getTick(){
        return tick;
    }

    /**
     * Retrieves the {@link Customer} specific Customer that order that book
     */
    public Customer getCustomer(){
        return customer;
    }

}
