package com.dydeve.data.example.test;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @Description:
 * @Date 下午2:03 2020/5/18
 * @Author: joker
 */
public class ChooseExecutors {

	private ChooseExecutors() {

	}

	private static final Logger log = LoggerFactory.getLogger(ChooseExecutors.class);

	public static ThreadFactory newGenericThreadFactory(String processName) {
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
			log.error("Unexpected exception in thread: " + t, e);
		};
		return (new ThreadFactoryBuilder())
				.setNameFormat(processName + "-%d")
				.setDaemon(true)
				.setUncaughtExceptionHandler(uncaughtExceptionHandler)
				.build();
	}

	public static final ThreadPoolExecutor es = new ThreadPoolExecutor(
			3,
			3,
			60,
			TimeUnit.SECONDS,
			new SynchronousQueue<>(),
			ChooseExecutors.newGenericThreadFactory("smart-choose"),
			new RejectedExecutionHandler() {

				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					System.out.println("############  clickhouse 忙");
				}
			});

	static {
		//es.allowCoreThreadTimeOut(true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> es.shutdown()));
	}

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 1000; i++) {
			es.execute(() -> {
				/*try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
				System.out.println("okokokokokokokokokok");
			});
		}

		TimeUnit.SECONDS.sleep(Long.MAX_VALUE);
	}

}
