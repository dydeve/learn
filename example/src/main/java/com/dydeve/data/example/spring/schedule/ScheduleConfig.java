package com.dydeve.data.example.spring.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Description:
 * @Date 下午3:50 2019/3/1
 * @Author: joker
 */
@EnableScheduling
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

	private static final Logger log = LoggerFactory.getLogger(ScheduleConfig.class);

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

		taskRegistrar.setScheduler(new ReshdeulingImmediatelyTaskScheduler(taskExecutor()));

		/**
		 * 曾经因为xql任务时长超过1小时(正常10-20min，可能由于xql服务端资源紧张)，导致漏跑下一小时的任务。
		 */
		taskRegistrar.addTriggerTask(() -> {}
		, new BaseOnLSETCronTrigger("0 50 * * * ? "));
	}

	/**
	 * xql并发度为6
	 * @return
	 */
	@Bean(destroyMethod = "shutdown")
	public ScheduledExecutorService taskExecutor() {
		ScheduledThreadPoolExecutor executorService = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
		log.warn("executorService:{}", executorService);
		return executorService;
	}
}