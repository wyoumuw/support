package com.youmu.support.spring.serviceinvoker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import com.google.common.collect.Sets;

public class ServiceDateConverter implements GenericConverter {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Sets.newHashSet(new ConvertiblePair(Date.class, String.class));
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		Date ds = (Date) source;
		if (null == ds) {
			return null;
		}
		return simpleDateFormat.format(ds);
	}
}