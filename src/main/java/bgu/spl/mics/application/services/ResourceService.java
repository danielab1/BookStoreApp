package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicle;
import bgu.spl.mics.application.messages.ReleaseVehicle;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private Queue<Future<DeliveryVehicle>> waitingForVehicle;

	public ResourceService() {
		super("Resource Service, Thread: " + Thread.currentThread().getId());
		resourcesHolder = ResourcesHolder.getInstance();
		waitingForVehicle = new LinkedBlockingQueue<>();
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicle.class, request -> {
			Future<DeliveryVehicle> ft = resourcesHolder.acquireVehicle();
			if(!ft.isDone())
				waitingForVehicle.add(ft);
			complete(request, ft);
		});

		subscribeEvent(ReleaseVehicle.class, vehicleHolder -> {
			resourcesHolder.releaseVehicle(vehicleHolder.getVehicle());
		});

		subscribeBroadcast(Terminate.class, request -> {
			waitingForVehicle.forEach(ft ->{
				if(!ft.isDone())
					ft.resolve(null);
			});
			terminate();
		});

		RegisteredServices.getInstance().incrementByOne();
	}

}
