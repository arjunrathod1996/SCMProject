package com.SCM.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class Utils2 {
	public static String getMessage(MessageSource messageSource, String code) {
		return messageSource.getMessage(code, null,LocaleContextHolder.getLocale());
	}
	public static boolean compare(Object o1,Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}
}

