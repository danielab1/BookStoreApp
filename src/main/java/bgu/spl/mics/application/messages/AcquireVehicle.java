package bgu.spl.mics.application.messages;


import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
/**
 * An Event sent by the {@link LogisticsService} Service to ask the for a Vehicle from ResourceHolder
 */

public class AcquireVehicle implements Event<Future<DeliveryVehicle>> {

    public AcquireVehicle(){}
}
