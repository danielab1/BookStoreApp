package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;


import static bgu.spl.mics.application.passiveObjects.OrderResult.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private MoneyRegister moneyRegister;
	private int currentTick;

	public SellingService() {
		super("SellingService, Thread: " + Thread.currentThread().getId());
		moneyRegister = MoneyRegister.getInstance();
		currentTick = 0;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(Tick.class,(currentTick)->{
			this.currentTick = currentTick.getTick();
		});

		subscribeEvent(BookOrderEvent.class, (orderEvent)-> {
			OrderResult res = null;
			Customer c = orderEvent.getCustomer();
			String bookName = orderEvent.getBookName();
			Future<Integer> ft = sendEvent(new CheckAvailabilityAndGetPrice(bookName));

			if (ft != null && ft.get() != null) {
				int price = ft.get();
				if (price == -1) {
					complete(orderEvent, null);
					return;
				}

				synchronized (c) {
					if (price <= c.getAvailableCreditAmount()) {
						Future<OrderResult> orderResultFuture = sendEvent(new TakeBook(bookName));
						if(orderResultFuture != null)
							res = orderResultFuture.get();
						if (res == SUCCESSFULLY_TAKEN)
							moneyRegister.chargeCreditCard(c, price);
					}
				}

				if (res == SUCCESSFULLY_TAKEN)  //Checking for the 2nd time since we wanted to release the synchronization on the client ASAP, and then create the receipt
					handleSuccesfulOrder(orderEvent, c, bookName, price);
				else complete(orderEvent, null);
			} else complete(orderEvent, null);
		});

		subscribeBroadcast(Terminate.class, request -> {
			terminate();
		});

		RegisteredServices.getInstance().incrementByOne();
	}

	public void handleSuccesfulOrder(BookOrderEvent orderEvent, Customer c, String bookName, int price){
		int id = c.getId();
		OrderReceipt receipt = new OrderReceipt(0, this.getName(), id, bookName, price, currentTick, orderEvent.getTick(), currentTick);;
		complete(orderEvent, receipt);
		moneyRegister.file(receipt);
		sendEvent(new DeliveryEvent(c.getAddress(), c.getDistance()));
	}

}
