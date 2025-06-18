package com.SCM.languageTranslation;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class LanguageConfigurer implements WebMvcConfigurer{
	
	private static final Logger logger = LoggerFactory.getLogger(LanguageConfigurer.class);
	
	
	@Bean
    public MessageSource messageSource() {
        try {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:langs/messages");
            messageSource.setDefaultEncoding("UTF-8");
            return messageSource;
        } catch (Exception e) {
            logger.error("Failed to configure MessageSource", e);
            throw new RuntimeException("Failed to configure MessageSource", e); // Optionally rethrow the exception
        }
    }

    @Bean
    public SessionLocaleResolver localeResolver() {
        try {
            SessionLocaleResolver localeResolver = new SessionLocaleResolver();
            localeResolver.setDefaultLocale(Locale.ENGLISH); // Set the default locale
            return localeResolver;
        } catch (Exception e) {
            logger.error("Failed to configure SessionLocaleResolver", e);
            throw new RuntimeException("Failed to configure SessionLocaleResolver", e); // Optionally rethrow the exception
        }
    }

    @Bean
    public LocaleChangeInterceptor localeInterceptor() {
        try {
            LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
            localeInterceptor.setParamName("lang");
            return localeInterceptor;
        } catch (Exception e) {
            logger.error("Failed to configure LocaleChangeInterceptor", e);
            throw new RuntimeException("Failed to configure LocaleChangeInterceptor", e); // Optionally rethrow the exception
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            registry.addInterceptor(localeInterceptor());
        } catch (Exception e) {
            logger.error("Failed to add interceptors", e);
            throw new RuntimeException("Failed to add interceptors", e); // Optionally rethrow the exception
        }
    }
	
}
