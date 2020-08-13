package com.dydeve.data.example.spring.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.TimeZone;

/**
 * @Date 下午6:23 2019/3/1
 * @Author: joker
 * @see CronTrigger
 */
public class BaseOnLSETCronTrigger implements Trigger {

	private static final Logger log = LoggerFactory.getLogger(BaseOnLSETCronTrigger.class);

	private final CronSequenceGenerator sequenceGenerator;

	/**
	 * Build a {@link BaseOnLSETCronTrigger} from the pattern provided in the default time zone.
	 *
	 * @param expression a space-separated list of time fields, following cron
	 *                   expression conventions
	 */
	public BaseOnLSETCronTrigger(String expression) {
		this.sequenceGenerator = new CronSequenceGenerator(expression);
	}

	/**
	 * Build a {@link BaseOnLSETCronTrigger} from the pattern provided in the given time zone.
	 *
	 * @param expression a space-separated list of time fields, following cron
	 *                   expression conventions
	 * @param timeZone   a time zone in which the trigger times will be generated
	 */
	public BaseOnLSETCronTrigger(String expression, TimeZone timeZone) {
		this.sequenceGenerator = new CronSequenceGenerator(expression, timeZone);
	}


	/**
	 * Determine the next execution time according to the given trigger context.
	 * <p>Next execution times are calculated based on the
	 * {@linkplain TriggerContext#lastScheduledExecutionTime completion time} of the
	 * previous execution; therefore, overlapping executions will occur on purpose.
	 */
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date date = triggerContext.lastScheduledExecutionTime();
		if (date == null) {
			date = new Date();
		}
		return this.sequenceGenerator.next(date);
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof BaseOnLSETCronTrigger &&
				this.sequenceGenerator.equals(((BaseOnLSETCronTrigger) other).sequenceGenerator)));
	}

	@Override
	public int hashCode() {
		return this.sequenceGenerator.hashCode();
	}

	@Override
	public String toString() {
		return this.sequenceGenerator.toString();
	}
}
