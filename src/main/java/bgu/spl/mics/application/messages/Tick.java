package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * An Event sent by the{@link TimeService} Service that represents current tick
 */
public class Tick implements Broadcast {
    private int tick;

    public Tick(int tick){
        this.tick = tick;
    }

    /**
     * Retrieves the current tick
     */
    public int getTick(){
        return tick;
    }
}
