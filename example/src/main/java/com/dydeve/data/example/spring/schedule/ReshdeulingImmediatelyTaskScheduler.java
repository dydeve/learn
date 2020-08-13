package com.dydeve.data.example.spring.schedule;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @Description:
 * @Date 下午6:34 2019/5/9
 * @Author: joker
 */
public class ReshdeulingImmediatelyTaskScheduler extends ConcurrentTaskExecutor implements TaskScheduler {

	private ScheduledExecutorService scheduledExecutor;

	@Nullable
	private ErrorHandler errorHandler;


	/**
	 * Create a new ConcurrentTaskScheduler,
	 * using a single thread executor as default.
	 *
	 * @see java.util.concurrent.Executors#newSingleThreadScheduledExecutor()
	 */
	public ReshdeulingImmediatelyTaskScheduler() {
		super();
		this.scheduledExecutor = initScheduledExecutor(null);
	}

	public ReshdeulingImmediatelyTaskScheduler(ScheduledExecutorService scheduledExecutor) {
		super(scheduledExecutor);
		this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
	}

	public ReshdeulingImmediatelyTaskScheduler(Executor concurrentExecutor, ScheduledExecutorService scheduledExecutor) {
		super(concurrentExecutor);
		this.scheduledExecutor = initScheduledExecutor(scheduledExecutor);
	}


	private ScheduledExecutorService initScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
		if (scheduledExecutor != null) {
			this.scheduledExecutor = scheduledExecutor;
		} else {
			this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		}
		return this.scheduledExecutor;
	}

	public void setScheduledExecutor(@Nullable ScheduledExecutorService scheduledExecutor) {
		initScheduledExecutor(scheduledExecutor);
	}

	/**
	 * Provide an {@link ErrorHandler} strategy.
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		Assert.notNull(errorHandler, "ErrorHandler must not be null");
		this.errorHandler = errorHandler;
	}


	@Override
	@Nullable
	public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
		try {
			ErrorHandler errorHandler =
					(this.errorHandler != null ? this.errorHandler : TaskUtils.getDefaultErrorHandler(true));
			return new ReschedulingImmediatelyRunnable(task, trigger, this.scheduledExecutor, errorHandler).schedule();
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
		long initialDelay = startTime.getTime() - System.currentTimeMillis();
		try {
			return this.scheduledExecutor.schedule(decorateTask(task, false), initialDelay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
		long initialDelay = startTime.getTime() - System.currentTimeMillis();
		try {
			return this.scheduledExecutor.scheduleAtFixedRate(decorateTask(task, true), initialDelay, period, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
		try {
			return this.scheduledExecutor.scheduleAtFixedRate(decorateTask(task, true), 0, period, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
		long initialDelay = startTime.getTime() - System.currentTimeMillis();
		try {
			return this.scheduledExecutor.scheduleWithFixedDelay(decorateTask(task, true), initialDelay, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
		try {
			return this.scheduledExecutor.scheduleWithFixedDelay(decorateTask(task, true), 0, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ex) {
			throw new TaskRejectedException("Executor [" + this.scheduledExecutor + "] did not accept task: " + task, ex);
		}
	}

	private Runnable decorateTask(Runnable task, boolean isRepeatingTask) {
		Runnable result = TaskUtils.decorateTaskWithErrorHandler(task, this.errorHandler, isRepeatingTask);
		return result;
	}

}