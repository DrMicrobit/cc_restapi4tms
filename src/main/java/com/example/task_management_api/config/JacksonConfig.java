package com.example.task_management_api.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class JacksonConfig {

    /**
     * Custom serializer for Instant that truncates to seconds (no milliseconds/nanoseconds)
     */
    public static class InstantSecondsSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                // Truncate to seconds and convert to ISO-8601 string (ends with Z)
                gen.writeString(value.truncatedTo(ChronoUnit.SECONDS).toString());
            }
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register the JavaTimeModule for Java 8 date/time types
        mapper.registerModule(new JavaTimeModule());

        // Create a custom module for Instant serialization
        SimpleModule instantModule = new SimpleModule();
        instantModule.addSerializer(Instant.class, new InstantSecondsSerializer());
        mapper.registerModule(instantModule);

        // Use ISO-8601 string format instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
