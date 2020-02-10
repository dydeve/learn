package com.dydeve.data.example.flume;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.apache.flume.interceptor.RegexExtractorInterceptorPassThroughSerializer;
import org.apache.flume.interceptor.RegexExtractorInterceptorSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Date 下午11:47 2020/1/7
 * @Author: joker
 */
public class RegexExtractorExtInterceptor implements Interceptor {
	static final String REGEX = "regex";
	static final String SERIALIZERS = "serializers";
	static final String EXTRACTOR_HEADER = "extractorHeader";
	static final boolean DEFAULT_EXTRACTOR_HEADER = false;
	static final String EXTRACTOR_HEADER_KEY = "extractorHeaderKey";

	private static final Logger logger = LoggerFactory
			.getLogger(RegexExtractorExtInterceptor.class);

	private final Pattern regex;
	private final List<NameAndSerializer> serializers;
	private final boolean extractorHeader;
	private final String extractorHeaderKey;

	private RegexExtractorExtInterceptor(Pattern regex,
	                                     List<NameAndSerializer> serializers,
	                                     boolean extractorHeader,
	                                     String extractorHeaderKey) {
		this.regex = regex;
		this.serializers = serializers;
		this.extractorHeader = extractorHeader;
		this.extractorHeaderKey = extractorHeaderKey;
	}

	@Override
	public void initialize() {
		// NO-OP...
	}

	@Override
	public void close() {}

	@Override
	public Event intercept(Event event) {
		String tmpStr;
		if (extractorHeader) {
			// 配置basenameHeader = true,basenameHeaderKey = fileName; extractorHeader = true,extractorHeaderKey = fileName
			tmpStr = event.getHeaders().get(extractorHeaderKey);
		} else {
			tmpStr = new String(event.getBody(), Charsets.UTF_8);
		}

		Matcher matcher = regex.matcher(tmpStr);

//		Matcher matcher = regex.matcher(
//				new String(event.getBody(), Charsets.UTF_8));
		Map<String, String> headers = event.getHeaders();
		if (matcher.find()) {
			for (int group = 0, count = matcher.groupCount(); group < count; group++) {
				int groupIndex = group + 1;
				if (groupIndex > serializers.size()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipping group {} to {} due to missing serializer",
								group, count);
					}
					break;
				}
				NameAndSerializer serializer = serializers.get(group);
				if (logger.isDebugEnabled()) {
					logger.debug("Serializing {} using {}", serializer.headerName,
							serializer.serializer);
				}
				headers.put(serializer.headerName,
						serializer.serializer.serialize(matcher.group(groupIndex)));
			}
		}
		return event;
	}

	@Override
	public List<Event> intercept(List<Event> events) {
		List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
		for (Event event : events) {
			Event interceptedEvent = intercept(event);
			if (interceptedEvent != null) {
				intercepted.add(interceptedEvent);
			}
		}
		return intercepted;
	}

	public static class Builder implements Interceptor.Builder {

		private Pattern regex;
		private List<NameAndSerializer> serializerList;
		private final RegexExtractorInterceptorSerializer defaultSerializer =
				new RegexExtractorInterceptorPassThroughSerializer();

		private boolean extractorHeader;
		private String extractorHeaderKey;

		@Override
		public void configure(Context context) {
			String regexString = context.getString(REGEX);
			Preconditions.checkArgument(!StringUtils.isEmpty(regexString),
					"Must supply a valid regex string");
			regex = Pattern.compile(regexString);
			regex.pattern();
			regex.matcher("").groupCount();


			extractorHeader = context.getBoolean(EXTRACTOR_HEADER, DEFAULT_EXTRACTOR_HEADER);
			if (extractorHeader) {
				extractorHeaderKey = context.getString(EXTRACTOR_HEADER_KEY);
				Preconditions.checkArgument(!StringUtils.isEmpty(extractorHeaderKey),
						"Must supply extractorHeaderKey");
			}

			configureSerializers(context);
		}

		private void configureSerializers(Context context) {
			String serializerListStr = context.getString(SERIALIZERS);
			Preconditions.checkArgument(!StringUtils.isEmpty(serializerListStr),
					"Must supply at least one name and serializer");

			String[] serializerNames = serializerListStr.split("\\s+");

			Context serializerContexts =
					new Context(context.getSubProperties(SERIALIZERS + "."));

			serializerList = Lists.newArrayListWithCapacity(serializerNames.length);
			for (String serializerName : serializerNames) {
				Context serializerContext = new Context(
						serializerContexts.getSubProperties(serializerName + "."));
				String type = serializerContext.getString("type", "DEFAULT");
				String name = serializerContext.getString("name");
				Preconditions.checkArgument(!StringUtils.isEmpty(name),
						"Supplied name cannot be empty.");

				if ("DEFAULT".equals(type)) {
					serializerList.add(new NameAndSerializer(name, defaultSerializer));
				} else {
					serializerList.add(new NameAndSerializer(name, getCustomSerializer(
							type, serializerContext)));
				}
			}
		}

		private RegexExtractorInterceptorSerializer getCustomSerializer(
				String clazzName, Context context) {
			try {
				RegexExtractorInterceptorSerializer serializer = (RegexExtractorInterceptorSerializer) Class
						.forName(clazzName).newInstance();
				serializer.configure(context);
				return serializer;
			} catch (Exception e) {
				logger.error("Could not instantiate event serializer.", e);
				Throwables.propagate(e);
			}
			return defaultSerializer;
		}

		@Override
		public Interceptor build() {
			Preconditions.checkArgument(regex != null,
					"Regex pattern was misconfigured");
			Preconditions.checkArgument(serializerList.size() > 0,
					"Must supply a valid group match id list");
			return new RegexExtractorExtInterceptor(regex, serializerList, extractorHeader, extractorHeaderKey);
		}
	}

	static class NameAndSerializer {
		private final String headerName;
		private final RegexExtractorInterceptorSerializer serializer;

		public NameAndSerializer(String headerName,
		                         RegexExtractorInterceptorSerializer serializer) {
			this.headerName = headerName;
			this.serializer = serializer;
		}
	}
}