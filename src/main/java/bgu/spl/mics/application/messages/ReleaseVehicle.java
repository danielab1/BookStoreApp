package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
/**
 * An Event sent by the {@link LogisticsService} Service to {@link ResourcesHolder} Release an Vehicle that complete an Delivery
 */

public class ReleaseVehicle implements Event<DeliveryVehicle> {

    private DeliveryVehicle dv;

    public ReleaseVehicle(DeliveryVehicle dv){
        this.dv = dv;
    }

    /**
     * Retrieves the Vehicle that complete the Delivery
     */
    public DeliveryVehicle getVehicle(){
        return dv;
    }
}
