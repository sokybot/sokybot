package org.sokybot.lockapiexample;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ServiceImp implements IService {

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();

	private Set<String> lockedParams = Collections.synchronizedSet(new HashSet<>());

	private Map<String, Integer> versionRegistry;

	public ServiceImp(Map<String, Integer> versionRegistry) {
		this.versionRegistry = versionRegistry;

	}

	private String getName() {
		return Thread.currentThread().getName();
	}

	@Override
	public boolean checkVersion(String param) {

		System.out.println(getName() + "-checkVersion" + " trying to check version for " + param);
		try {

			System.out.println(getName() + "-checkVersion" + " trying to get lock");
			this.readLock.lock();
			System.out.println(getName() + "-checkVersion" + " acquired lock ");

			System.out.println(getName() + "-checkVersion" + " found lockedParams like "
					+ Arrays.toString(this.lockedParams.toArray()));
			while (this.lockedParams.contains(param)) {
				System.out.println(getName() + "-checkVersion" + " wait until isParamLocked condition signal ");
				//isParamLocked.await();
				System.out.println(getName() + "-checkVersion" + " wake up ...");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.readLock.unlock();

		}
		return !isNeedUpdate(param);

	}

	private boolean isNeedUpdate(String param) {
		if (!this.versionRegistry.containsKey(param) || this.versionRegistry.get(param) < 3) {
			return true;
		}

		return false;
	}

	private int updateRepositories() {
		System.out.println("Updating repositories");
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(5));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 3;
	}

	@Override
	public void update(String param) {

		System.out.println(getName() + " start update operation ");

		try {
			System.out.println(getName() + " trying to get lock");
			lock.lock();
			System.out.println(getName() + " acquried lock");
			if (isNeedUpdate(param)) {
				this.lockedParams.add(param);
				System.out.println(getName() + " put " + param + " into param lock memeory");

				int newVersion = updateRepositories();
				System.out.println(getName() + "Update Version Registry with new version " + newVersion);
				this.versionRegistry.put(param, newVersion);

			} else {
				System.out.println(param + " not need update");
				System.out.println(getName() + " release lock");
				lock.unlock();
				System.out.println(getName() + " returning");
				return;
			}

		} finally {
			System.out.println(getName() + " remove param");
			this.lockedParams.remove(param);

			System.out.println(getName() + " signal condition");
			this.isParamLocked.signalAll();
			System.out.println(getName() + " release lock");
			lock.unlock();

		}

	}

	private static class Consumer extends Thread {

		private IService service;
		private String staticParam;

		public Consumer(IService service, String staticParam) {
			this.service = service;
			this.staticParam = staticParam;
			setName("Consumer-Thread");
		}

		@Override
		public void run() {

			this.service.checkVersion(staticParam);

		}

	}

	public static void main(String args[]) {

		Map<String, Integer> gameVersionRepo = new HashMap<>();
		gameVersionRepo.put("isro", 1);
		gameVersionRepo.put("vsro", 2);

		IService service = new ServiceImp(gameVersionRepo);
		Producer producer = new Producer(service, "isro");
		Producer producer2 = new Producer(service, "isro");

		Consumer consumer = new Consumer(service, "isro");
		RandomParamConsumer rndConsumer = new RandomParamConsumer(service);

		producer.start();
		producer2.start();

		consumer.start();
		// rndConsumer.start();

	}
}
