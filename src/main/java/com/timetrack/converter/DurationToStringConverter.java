package com.timetrack.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Converts a Duration object to a string in the format HH:mm.
 */
@Component
public class DurationToStringConverter implements Converter<Duration, String> {
    @Override
    public String convert(@Nullable Duration source) {
        if (source == null) {
            return "";
        }
        long hours = source.toHours();
        long minutes = source.minusHours(hours).toMinutes();
        return String.format(
                "%02d:%02d", hours, minutes
        );
    }
}