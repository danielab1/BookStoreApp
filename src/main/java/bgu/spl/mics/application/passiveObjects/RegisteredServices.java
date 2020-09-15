package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Singleton Class to count the number of registered devices
 * Used to keep track of registered device so they all get first Tick
 */

public class RegisteredServices {

    private AtomicInteger registered;

    private static class SingletonHolder {
        private static RegisteredServices instance = new RegisteredServices();
    }

    private RegisteredServices(){
        registered = new AtomicInteger(0);
    }

    public static RegisteredServices getInstance() {
        return SingletonHolder.instance;
    }

    /** increment the Num of the registered devices
     */
    public void incrementByOne(){
        registered.incrementAndGet();
    }

    /** @return the Num of the registered devices
     */
    public int getRegistered() {
        return registered.get();
    }

    public void initializeRegistered(){ registered = new AtomicInteger(0); }

}
