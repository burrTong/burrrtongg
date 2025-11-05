package com.example.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    // Formatter for the user-provided format: "2025-11-05 14:32:28.274513+00"
    private static final DateTimeFormatter CUSTOM_OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSX");
    // Formatter for ISO_LOCAL_DATE_TIME (e.g., "2025-11-05T14:32:28.274513")
    private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    // Formatter for ISO_LOCAL_DATE (e.g., "2025-11-05")
    private static final DateTimeFormatter ISO_LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
            builder.deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    String value = p.getText();
                    try {
                        // Try parsing the user-provided format with offset
                        return OffsetDateTime.parse(value, CUSTOM_OFFSET_DATE_TIME_FORMATTER).toLocalDateTime();
                    } catch (DateTimeParseException e) {
                        try {
                            // Try parsing ISO_LOCAL_DATE_TIME
                            return LocalDateTime.parse(value, ISO_LOCAL_DATE_TIME_FORMATTER);
                        } catch (DateTimeParseException ex) {
                            try {
                                // Try parsing ISO_LOCAL_DATE and convert to start of day
                                return LocalDate.parse(value, ISO_LOCAL_DATE_FORMATTER).atStartOfDay();
                            } catch (DateTimeParseException exc) {
                                throw new IOException("Unable to parse LocalDateTime from: " + value, exc);
                            }
                        }
                    }
                }
            });
        };
    }
}
