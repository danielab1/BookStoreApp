package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private HashMap<Integer, BlockingQueue<String>> orderSchedule;
	private BlockingQueue<Future<OrderReceipt>> currentTickFutures;
	private Customer customer;

	public APIService(Customer customer, HashMap<Integer, BlockingQueue<String>> orderSchedule) {
		super("API Service, Thread: " + Thread.currentThread().getId());
		this.orderSchedule = orderSchedule;
		this.currentTickFutures = new LinkedBlockingQueue<>();
		this.customer = customer;
	}


	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class, (currentTick)->{
			//everyTick, send BookOrderEvent according to current tick number
			BlockingQueue<String> booksQueue = orderSchedule.get(currentTick.getTick());
			if(booksQueue != null){
				while(!booksQueue.isEmpty()) {
					try {
						// Sends the BookOrderEvent without waiting for completion, holds the Future Results in 'currentTickFutures' Queue
						Future<OrderReceipt> ft = sendEvent(new BookOrderEvent(customer, booksQueue.take(), currentTick.getTick()));
						if(ft!=null)
							currentTickFutures.put(ft);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Handles all the orderings that were made earlier, without lingering on each one
				// take FutureObj, first checks if the future was resolved, otherwise adds it back to the end of the line
				while(!currentTickFutures.isEmpty()){
					try {
						Future<OrderReceipt> ft = currentTickFutures.take();
						if(!ft.isDone())
							currentTickFutures.put(ft);
						else {
							OrderReceipt receipt= ft.get();
							if(receipt != null)
								customer.addReceipt(receipt);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		subscribeBroadcast(Terminate.class, request -> {
			terminate();
		});

		RegisteredServices.getInstance().incrementByOne();
	}

}
