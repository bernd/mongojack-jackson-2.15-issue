package org.example.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.junit.jupiter.api.Test;
import org.mongojack.JacksonMongoCollection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleTest extends MongojackTestCase {
    @Test
    void test() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final JacksonMongoCollection<Metric> metrics = JacksonMongoCollection.builder()
                .withObjectMapper(objectMapper)
                .build(mongoDatabase, "metrics", Metric.class, UuidRepresentation.STANDARD);

        final Metric metric = new Metric("metric-1", 123, 0.8);

        assertInstanceOf(Metric.class, objectMapper.readValue(objectMapper.writeValueAsString(metric), Metric.class));

        metrics.insertOne(metric);

        final Metric metricRecord = metrics.find(Filters.eq("name", "metric-1")).first();

        assertInstanceOf(Metric.class, metricRecord);
        assertNotNull(metricRecord);
        assertEquals(123, metricRecord.getCount());
        assertEquals(0.8, metricRecord.getPercentage());
    }

    @JsonIgnoreProperties("_id")
    public static class Metric {
        private final String name;
        private final int count;
        private final double percentage;

        public Metric(@JsonProperty("name") String name,
                      @JsonProperty("count") int count,
                      @JsonProperty("percentage") double percentage) {
            this.name = name;
            this.count = count;
            this.percentage = percentage;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("count")
        public int getCount() {
            return count;
        }

        @JsonProperty("percentage")
        public double getPercentage() {
            return percentage;
        }
    }
}