package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
		private Queue<DeliveryVehicle> availableVehicles;
		private Queue<Future<DeliveryVehicle>> waitingForVehicle;
		private Semaphore sm;
	/**
	 * Retrieves the single instance of this class.
	 */
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	public static ResourcesHolder getInstance() { return SingletonHolder.instance; }

	private ResourcesHolder() {
		availableVehicles = new ConcurrentLinkedQueue<>();
		waitingForVehicle = new ConcurrentLinkedQueue<>();
	}
	/**
	 * Tries to acquire a vehicle and gives a future object which will
	 * resolve to a vehicle.
	 * <p>
	 * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a
	 * 			{@link DeliveryVehicle} when completed.
	 */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> ft = new Future<>();
		waitingForVehicle.add(ft);
		if(sm.tryAcquire()) {
			if(!ft.isDone())
				ft.resolve(availableVehicles.poll());
		}

		return ft;
	}

	/**
	 * Releases a specified vehicle, opening it again for the possibility of
	 * acquisition.
	 * <p>
	 * @param vehicle	{@link DeliveryVehicle} to be released.
	 */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		if(vehicle != null){
			while(!waitingForVehicle.isEmpty()){
				Future<DeliveryVehicle> ft = waitingForVehicle.poll();
				if(ft != null && !ft.isDone()){
					ft.resolve(vehicle);
					return;
				}
			}
			availableVehicles.add(vehicle);
			sm.release();
		}


	}

	/**
	 * Receives a collection of vehicles and stores them.
	 * <p>
	 * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
	 */
	public void load(DeliveryVehicle[] vehicles) {
		availableVehicles.addAll(Arrays.asList(vehicles));
		sm = new Semaphore(availableVehicles.size(), true);
	}
}