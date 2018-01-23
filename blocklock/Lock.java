package blocklock;

import net.spy.memcached.MemcachedClient;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Lock {

	private MemcachedClient mcc;

	private final String DEFAULT_VALUE = "1";

	private final int POLLING_SLEEP_TIME = 100;

	public Lock(MemcachedClient mcc) {
		this.mcc = mcc;
	}

	/**
	 * 要确保再过期时间内完成 解锁操作 并且为了防止死锁 必须设定超时时间
	 * 
	 * @param exp lock有效时间 用来防止死锁
	 * @param timeout >0 超时未获取返回false 
	 * @return 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean lock(String key, int exp, int timeout) throws InterruptedException, ExecutionException {
		Future<Boolean> fo = mcc.add(key, exp, DEFAULT_VALUE);
		long nowM = System.currentTimeMillis();
		boolean cont = true;
		while (!fo.get() && cont) {// 未锁定 并且未达到超时时间 轮询
			Thread.sleep(POLLING_SLEEP_TIME);
			fo = mcc.add(key, exp, DEFAULT_VALUE);
			cont = timeout > 0 ? ((System.currentTimeMillis() - nowM) < timeout) : true;
		}
		return fo.get();
	}

	/**
	 * 是否存在误删除情况？
	 * 
	 * @param key
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public boolean unLock(String key) throws InterruptedException, ExecutionException {
		Future<Boolean> fo = mcc.delete(key);
		return fo.get();
	}

}
