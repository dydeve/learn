package com.dydeve.data.example.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * @Description:
 * @Date 下午10:31 2019/12/22
 * @Author: joker
 */
public class ConfigWatcher implements Watcher {

	private ReadWriteZookeeper zk;
	private static final String path = "/config";


	public ConfigWatcher(String hosts) throws InterruptedException {
		zk = new ReadWriteZookeeper();
		zk.connection(hosts);
	}

	public void displayConfig() throws KeeperException, InterruptedException {
		String data = zk.readData(path, this, null);
		System.out.println("read:" + data);
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == Event.EventType.NodeDataChanged) {
			try {
				displayConfig();
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws InterruptedException, KeeperException {
		ConfigWatcher watcher = new ConfigWatcher("192.168.0.1:2181,192.168.0.2:2181");
		watcher.displayConfig();//注册监听器
		//让监听器一直处于监听状态
		Thread.sleep(Long.MAX_VALUE);
	}

}
