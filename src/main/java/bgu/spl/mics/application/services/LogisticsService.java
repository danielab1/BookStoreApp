package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	public LogisticsService() {
		super("Logistics Service, Thread: " + Thread.currentThread().getId());
	}

	@Override
	protected void initialize() {

		subscribeEvent(DeliveryEvent.class, Deliver-> {
			Future<Future<DeliveryVehicle>> ft = sendEvent(new AcquireVehicle());
			DeliveryVehicle dv = null;
			if(ft != null && ft.get() != null && ft.get().get() != null){
				dv = ft.get().get();
				dv.deliver(Deliver.getAddress(), Deliver.getDistance());
				sendEvent(new ReleaseVehicle(dv));
			}

		});

		subscribeBroadcast(Terminate.class, request -> {
			terminate();
		});

		RegisteredServices.getInstance().incrementByOne();
	}
}