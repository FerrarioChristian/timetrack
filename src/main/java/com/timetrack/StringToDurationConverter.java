package com.timetrack;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class StringToDurationConverter implements Converter<String, Duration> {
    @Override
    public Duration convert(String source) {
        try {
            if (source.isEmpty()) {
                return null;
            }
            String[] parts = source.split(":");
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            return Duration.ofHours(hours).plusMinutes(minutes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid duration format. Expected HH:mm");
        }
    }
}