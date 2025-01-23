package com.timetrack;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToDurationConverter stringToDurationConverter;

    public WebConfig(StringToDurationConverter stringToDurationConverter) {
        this.stringToDurationConverter = stringToDurationConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToDurationConverter);
    }
}