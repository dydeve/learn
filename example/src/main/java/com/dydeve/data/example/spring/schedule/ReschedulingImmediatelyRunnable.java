package com.dydeve.data.example.spring.schedule;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.Assert;
import org.springframework.util.ErrorHandler;

import java.util.Date;
import java.util.concurrent.*;

/**
 * @Description:
 * @Date 下午5:50 2019/5/9
 * @Author: joker
 */
public class ReschedulingImmediatelyRunnable extends DelegatingErrorHandlingRunnable implements ScheduledFuture<Object> {

	private final Trigger trigger;

	private final SimpleTriggerContext triggerContext = new SimpleTriggerContext();

	private final ScheduledExecutorService executor;

	@Nullable
	private ScheduledFuture<?> currentFuture;

	@Nullable
	private Date scheduledExecutionTime;

	private final Object triggerContextMonitor = new Object();


	public ReschedulingImmediatelyRunnable(
			Runnable delegate, Trigger trigger, ScheduledExecutorService executor, ErrorHandler errorHandler) {

		super(delegate, errorHandler);
		this.trigger = trigger;
		this.executor = executor;
	}


	@Nullable
	public ScheduledFuture<?> schedule() {
		synchronized (this.triggerContextMonitor) {
			this.scheduledExecutionTime = this.trigger.nextExecutionTime(this.triggerContext);
			if (this.scheduledExecutionTime == null) {
				return null;
			}
			long initialDelay = this.scheduledExecutionTime.getTime() - System.currentTimeMillis();
			this.currentFuture = this.executor.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			return this;
		}
	}

	private ScheduledFuture<?> obtainCurrentFuture() {
		Assert.state(this.currentFuture != null, "No scheduled future");
		return this.currentFuture;
	}

	@Override
	public void run() {
		Date actualExecutionTime = new Date();

		this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, null);

		if (!obtainCurrentFuture().isCancelled()) {
			schedule();
		}

		super.run();
		Date completionTime = new Date();
		synchronized (this.triggerContextMonitor) {
			Assert.state(this.scheduledExecutionTime != null, "No scheduled execution");
			this.triggerContext.update(this.scheduledExecutionTime, actualExecutionTime, completionTime);

		}
	}


	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (this.triggerContextMonitor) {
			return obtainCurrentFuture().cancel(mayInterruptIfRunning);
		}
	}

	@Override
	public boolean isCancelled() {
		synchronized (this.triggerContextMonitor) {
			return obtainCurrentFuture().isCancelled();
		}
	}

	@Override
	public boolean isDone() {
		synchronized (this.triggerContextMonitor) {
			return obtainCurrentFuture().isDone();
		}
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = obtainCurrentFuture();
		}
		return curr.get();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = obtainCurrentFuture();
		}
		return curr.get(timeout, unit);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		ScheduledFuture<?> curr;
		synchronized (this.triggerContextMonitor) {
			curr = obtainCurrentFuture();
		}
		return curr.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed other) {
		if (this == other) {
			return 0;
		}
		long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
		return (diff == 0 ? 0 : ((diff < 0)? -1 : 1));
	}

}