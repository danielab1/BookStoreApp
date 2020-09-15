package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private Map<Class<? extends Event>, BlockingQueue<MicroService>> eventsSubscribers;
	private Map<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastsSubscribers;
	private Map<MicroService, BlockingQueue<Message>> microsPersonalQueue;
	private Map<Message, Future> futuresObjects;


	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	private MessageBusImpl() {
		eventsSubscribers = new ConcurrentHashMap<>();
		broadcastsSubscribers = new ConcurrentHashMap<>();
		microsPersonalQueue = new ConcurrentHashMap<>();
		futuresObjects = new ConcurrentHashMap<>();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		try {
			eventsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());
			eventsSubscribers.get(type).put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		try {
			broadcastsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());
			broadcastsSubscribers.get(type).put(m);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futuresObjects.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> queueReference = broadcastsSubscribers.get(b.getClass());
		if(queueReference == null)
			return ;
		queueReference.forEach((MicroService ms) ->{
			try {
				microsPersonalQueue.get(ms).put(b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		BlockingQueue<MicroService> queueReference = eventsSubscribers.get(e.getClass());
		Future<T> ft = null;
		try {
			if(!queueReference.isEmpty()){
				synchronized (queueReference){
					ft = new Future<>();
					futuresObjects.put(e, ft);
					MicroService ms = queueReference.take();
					microsPersonalQueue.get(ms).put(e);
					queueReference.put(ms);

				}
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		return ft;
	}

	@Override
	public void register(MicroService m) {
		microsPersonalQueue.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {

		// Its reference in eventsMsQueue
		((ConcurrentHashMap)eventsSubscribers).forEachValue(1, queue -> {
			synchronized (queue){
				((BlockingQueue)queue).remove(m);
			}
		});

		// His Event queue in msMessagesQueue
		BlockingQueue<Message> messagesQueue = microsPersonalQueue.get(m);
		messagesQueue.forEach(event -> {
			complete((Event)event, null);
		});

		// Its reference in broadcastsMsQueue
		((ConcurrentHashMap)broadcastsSubscribers).forEachValue(1, queue -> {
			((BlockingQueue)queue).remove(m);
		});

		// Finds the ms Messages Queue
		microsPersonalQueue.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return (Message)((BlockingQueue)microsPersonalQueue.get(m)).take();
	}



}
