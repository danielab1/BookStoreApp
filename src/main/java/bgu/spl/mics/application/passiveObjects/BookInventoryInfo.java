package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo implements Serializable {
	private String bookTitle;
	private AtomicInteger amount;
	private int price;
	private Semaphore semaphore;

	public BookInventoryInfo(String bookTitle, int amount, int price){
		this.bookTitle = bookTitle;
		this.amount = new AtomicInteger(amount);
		this.price = price;
		this.semaphore = new DynamicSemaphore(amount, true);
	}
	/**
	 * Initializng Semaphore explicitly apart for case when initiating straight with gson
	 */
	public void initializeSemaphore(){
		semaphore = new DynamicSemaphore(this.amount.get(), true);
	}

	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
	 * Retrieves the amount of books of this type in the inventory.
	 * <p>
	 * @return amount of available books.
	 */
	public int getAmountInInventory() {
		return amount.get();
	}

	/**
	 * Retrieves the price for  book.
	 * <p>
	 * @return the price of the book.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Reduce the Amount of the book in the inventory and also reduce Semaphore permits
	 */
	public void reduceAmountOfBooksByOne(){
		this.amount.decrementAndGet();
		((DynamicSemaphore)semaphore).reducePermitsByOne();
		semaphore.release();
	}
	/**
	 * Retreive specific Semaphore
	 * @return the Semaphore object associated with the specific book
	 */
	public Semaphore getSemaphore(){
		return semaphore;
	}


}
