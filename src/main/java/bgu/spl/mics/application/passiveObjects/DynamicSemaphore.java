package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;

/**
 * The {@link DynamicSemaphore class was created to use Semaphore's protected function 'reducePermits'
 */
public class DynamicSemaphore extends Semaphore {

    public DynamicSemaphore(int amount, boolean fair){
        super(amount, fair);
    }

    /**
     * reduces permits by one
     */
    public void reducePermitsByOne(){ this.reducePermits(1); }
}
