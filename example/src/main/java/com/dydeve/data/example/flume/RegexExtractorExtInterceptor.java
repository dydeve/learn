package com.dydeve.data.example.flume;

import org.apache.flume.interceptor.Interceptor;

import java.util.List;

/**
 * @Description:
 * @Date 下午11:47 2020/1/7
 * @Author: joker
 */
public class RegexExtractorExtInterceptor implements Interceptor {


	@Override
	public void initialize() {

	}

	@Override
	public org.apache.flume.Event intercept(org.apache.flume.Event event) {
		return null;
	}

	@Override
	public List<org.apache.flume.Event> intercept(List<org.apache.flume.Event> list) {
		return null;
	}

	@Override
	public void close() {

	}
}
