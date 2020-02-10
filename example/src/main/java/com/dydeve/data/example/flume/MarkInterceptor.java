package com.dydeve.data.example.flume;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;

/**
 * @Description:
 * @Date 下午11:20 2020/1/13
 * @Author: joker
 */
public class MarkInterceptor implements Interceptor {

	static final String BIZ_TYPE = "bizType";

	@Override
	public void initialize() {

	}

	@Override
	public Event intercept(Event event) {
		if (event == null) {
			return null;
		}

		try {
			String body = new String(event.getBody());

			String bizType = event.getHeaders().get(BIZ_TYPE);
			if (StringUtils.isNotEmpty(bizType)) {
				bizType = "none";
			}
			body = bizType + "," + body;
			event.setBody(body.getBytes());

			return event;
		} catch (Exception e) {
			e.printStackTrace();
			return event;
		}

	}

	@Override
	public List<Event> intercept(List<Event> list) {
		List<Event> intercepted = Lists.newArrayListWithCapacity(list.size());
		for (Event event : list) {
			Event interceptedEvent = intercept(event);
			if (interceptedEvent != null) {
				intercepted.add(interceptedEvent);
			}
		}
		return intercepted;
	}

	@Override
	public void close() {

	}

	public static class MarkBuilder implements Interceptor.Builder {

		@Override
		public Interceptor build() {
			return new MarkInterceptor();
		}

		@Override
		public void configure(Context context) {
		}
	}
}
