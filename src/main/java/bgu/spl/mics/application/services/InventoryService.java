package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityAndGetPrice;
import bgu.spl.mics.application.messages.TakeBook;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.Tick;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private Inventory inventory;

	public InventoryService() {
		super("Inventory, Thread: " + Thread.currentThread().getId());
		inventory = Inventory.getInstance();
	}

	@Override
	protected void initialize() {

		subscribeBroadcast(Tick.class,(currentTick)->{});

		subscribeEvent(CheckAvailabilityAndGetPrice.class, bookToCheck->{
			int price = inventory.checkAvailabiltyAndGetPrice(bookToCheck.getName());
			complete(bookToCheck, price);
		});

		subscribeEvent(TakeBook.class,bookToTake->{
			OrderResult result = inventory.take(bookToTake.getName());
			complete(bookToTake,result);
		});

		subscribeBroadcast(Terminate.class, request -> {
			terminate();
		});

		RegisteredServices.getInstance().incrementByOne();
		
	}

}
