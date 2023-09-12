package org.example.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.Filters;
import org.bson.UuidRepresentation;
import org.junit.jupiter.api.Test;
import org.mongojack.JacksonMongoCollection;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class JsonSubtypesTest extends MongojackTestCase {
    @Test
    void test() throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();

        final JacksonMongoCollection<Person> people = JacksonMongoCollection.builder()
                .withObjectMapper(objectMapper)
                .build(mongoDatabase, "people", Person.class, UuidRepresentation.STANDARD);

        final PolitePerson politePerson = new PolitePerson(1); // Person with an "int" politeness
        final GrumpyPerson grumpyPerson = new GrumpyPerson(0.1); // Person with a "double" politeness

        // The serialize/deserialize cycle works when just plain Jackson
        assertInstanceOf(GrumpyPerson.class, objectMapper.readValue(objectMapper.writeValueAsString(grumpyPerson), Person.class));
        assertInstanceOf(PolitePerson.class, objectMapper.readValue(objectMapper.writeValueAsString(politePerson), Person.class));

        people.insertOne(politePerson);
        people.insertOne(grumpyPerson);

        final PolitePerson politePersonRecord = (PolitePerson) people.find(Filters.eq("type", "POLITE")).first();
        final GrumpyPerson grumpyPersonRecord = (GrumpyPerson) people.find(Filters.eq("type", "GRUMPY")).first();

        assertInstanceOf(PolitePerson.class, politePersonRecord);
        assertInstanceOf(GrumpyPerson.class, grumpyPersonRecord);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GrumpyPerson.class, name = "GRUMPY"),
            @JsonSubTypes.Type(value = PolitePerson.class, name = "POLITE"),
    })
    @JsonIgnoreProperties("_id")
    public static abstract class Person {

        @JsonProperty("type")
        public abstract String getType();
    }

    public static class GrumpyPerson extends Person {
        private final double politeness;

        public GrumpyPerson(@JsonProperty("politeness") double politeness) {
            this.politeness = politeness;
        }

        @JsonProperty("politeness")
        public double getPoliteness() {
            return politeness;
        }

        @Override
        public String getType() {
            return "GRUMPY";
        }
    }

    public static class PolitePerson extends Person {
        private final int politeness;

        public PolitePerson(@JsonProperty("politeness") int politeness) {
            this.politeness = politeness;
        }

        @JsonProperty("politeness")
        public int getPoliteness() {
            return politeness;
        }

        @Override
        public String getType() {
            return "POLITE";
        }
    }
}
