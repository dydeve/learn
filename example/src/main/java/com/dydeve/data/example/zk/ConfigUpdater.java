package com.dydeve.data.example.zk;

import org.apache.zookeeper.KeeperException;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Date 下午11:03 2019/12/22
 * @Author: joker
 */
public class ConfigUpdater {

	private static final String path = "/config";
	private Random _random = new Random();
	private ReadWriteZookeeper zk;

	public ConfigUpdater(String hosts) throws InterruptedException {
		zk = new ReadWriteZookeeper();
		zk.connection(hosts);
	}

	public void run() throws KeeperException, InterruptedException {
		while (true) {
			String data  = String.valueOf(_random.nextInt(100));
			zk.setData(path, data);
			System.out.println("write:" + data);
			TimeUnit.SECONDS.sleep(_random.nextInt(10));
		}

	}

	public static void main(String[] args) throws InterruptedException, KeeperException {
		ConfigUpdater updater = new ConfigUpdater("*:2181,*:2181");
		updater.run();
	}

}
