package com.dydeve.data.example.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @Description:
 * @Date 下午10:41 2019/12/19
 * @Author: joker
 */
public class ConnectionWatcher implements Watcher {

	private static final int SESSION_TIMEOUT = 5000;
	protected ZooKeeper zk;
	private CountDownLatch _connectionSignal = new CountDownLatch(1);

	public void connection(String connectString) throws InterruptedException {
		try {
			zk = new ZooKeeper(connectString, SESSION_TIMEOUT, this);
			_connectionSignal.await();
		} catch (IOException e) {

		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == Event.KeeperState.SyncConnected) {
			//等待zookeeper连接完成，释放等待
			_connectionSignal.countDown();
		}
	}

	public void close() throws InterruptedException {
		zk.close();
	}
}
