package com.timetrack.config;

import com.timetrack.converter.DurationToStringConverter;
import com.timetrack.converter.StringToDurationConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToDurationConverter stringToDurationConverter;
    private final DurationToStringConverter durationToStringConverter;


    public WebConfig(StringToDurationConverter stringToDurationConverter, DurationToStringConverter durationToStringConverter) {
        this.stringToDurationConverter = stringToDurationConverter;
        this.durationToStringConverter = durationToStringConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToDurationConverter);
        registry.addConverter(durationToStringConverter);
    }
}