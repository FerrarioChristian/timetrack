package com.timetrack.converter;

import io.micrometer.common.lang.NonNull;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

/**
 * Formatter for {@link Duration} objects.
 */
@Component
public class DurationFormatter implements Formatter<Duration> {

    /**
     * Parses a {@link String} into a {@link Duration} object.
     *
     * @param text   the text to parse
     * @param locale the locale to use
     * @return the parsed {@link Duration} object
     */
    @Override
    public @NonNull Duration parse(@NonNull String text, @NonNull Locale locale) {
        return Objects.requireNonNull(new StringToDurationConverter().convert(text));
    }

    /**
     * Formats a {@link Duration} object into a {@link String}.
     *
     * @param duration the {@link Duration} object to format
     * @param locale   the locale to use
     * @return the formatted {@link String}
     */
    @Override
    public @NonNull String print(Duration duration, @NonNull Locale locale) {
        if (duration.isZero() || duration.isNegative()) {
            return "N/A";
        }
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder formatted = new StringBuilder();

        if (days > 0) formatted.append(days).append("d ");
        if (hours > 0) formatted.append(hours).append("h ");
        if (minutes > 0) formatted.append(minutes).append("m ");
        if (seconds > 0 || formatted.isEmpty()) formatted.append(seconds).append("s");

        return formatted.toString().trim();
    }
}
