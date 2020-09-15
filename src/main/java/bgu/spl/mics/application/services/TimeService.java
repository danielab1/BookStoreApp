package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.Terminate;
import bgu.spl.mics.application.messages.Tick;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private int tick;

	public TimeService(int speed, int duration) {
		super("Timer Service, Thread: " + Thread.currentThread().getId());
		this.speed = speed;
		this.duration = duration;
		this.tick = 1;
	}

	@Override
	protected void initialize() {
		while(tick <= duration) {
			try {
				sendBroadcast(new Tick(tick));
				sleep(speed);

				tick++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		sendBroadcast(new Terminate());
		terminate();
	}


}
