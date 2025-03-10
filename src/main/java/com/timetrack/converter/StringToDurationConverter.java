package com.timetrack.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Converts a string in the format HH:mm to a Duration object.
 */
@Component
public class StringToDurationConverter implements Converter<String, Duration> {
    @Override
    public Duration convert(@Nullable String source) {
        try {
            if (source == null || source.isEmpty()) {
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