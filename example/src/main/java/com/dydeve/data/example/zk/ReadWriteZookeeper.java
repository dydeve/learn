package com.dydeve.data.example.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Date 上午12:09 2019/12/20
 * @Author: joker
 */
public class ReadWriteZookeeper extends ConnectionWatcher {

	private static final Charset CHARSET = Charset.forName("UTF-8");

	private static Stat stat = new Stat();


	public String create(String path, String data) throws KeeperException, InterruptedException {
		return zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	public Stat exist(String path, boolean watch) throws KeeperException, InterruptedException {
		return zk.exists(path, watch);
	}

	public String getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
		byte[] data = zk.getData(path, watch, stat);
		return new String(data, CHARSET);
	}

	/**
	 * If the watch is non-null and the call is successful (no exception is thrown),
	 * a watch will be left on the node with the given path.
	 * The watch will be triggered by a successful operation that sets data on the node, or deletes the node.
	 *
	 * watcher 每次监听完，就要重置
	 *
	 */
	public String readData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
		byte[] data = zk.getData(path, watcher, stat);
		return new String(data, CHARSET);
	}

	public Stat setData(String path, String data) throws KeeperException, InterruptedException {
		//if the given version is -1, it matches any node's versions
		return zk.setData(path, data.getBytes(), -1);
	}

	public void getChildren(String path) throws KeeperException, InterruptedException {
		List<String> children = zk.getChildren(path, false);
		for (String znode : children) {
			System.out.println("znode:" + znode);
		}
	}

	/**
	 * 必须先删子节点，然后删父节点
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void deleteNode() throws KeeperException, InterruptedException {
		zk.delete("/test/app1", -1);
		zk.delete("/test/app2", -1);
		zk.delete("/test", -1);
		System.out.println("delete it");
	}

	public static void main(String[] args) throws InterruptedException, KeeperException {
		ReadWriteZookeeper zk = new ReadWriteZookeeper();
		zk.connection("192.168.0.1:2181,192.168.1.1:2181");

		String result = zk.create("/test", "hello");
		System.out.println(result);

		Stat stat = zk.exist("/test", false);
		System.out.println(stat.getAversion());//0

		String data = zk.getData("/test", false, stat);
		System.out.println(data);
		System.out.println(stat.getAversion());

		zk.setData("/test", "world");

		result = zk.create("/test/app1", "hello");
		result = zk.create("/test/app2", "hello");
		System.out.println(result);

		zk.getChildren("/test");
	}

}
