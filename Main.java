import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import blocklock.*;
import net.spy.memcached.MemcachedClient;

public class Main {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub

		try {
			MemcachedClient mcc = new MemcachedClient(new InetSocketAddress("192.168.1.200", 11211));
			Lock lk = new Lock(mcc);
			System.out.println(lk.lock("test", 10, 200));
			//System.out.println(lk.unLock("test"));
			System.out.println(lk.lock("test", 1, 0));
			mcc.shutdown();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

}
