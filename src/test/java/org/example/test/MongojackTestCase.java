package org.example.test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class MongojackTestCase {
    @Container
    MongoDBContainer mongodbInstance = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;

    @BeforeEach
    void setUp() {
        this.mongoClient = MongoClients.create(mongodbInstance.getConnectionString());
        this.mongoDatabase = mongoClient.getDatabase("test");
    }

    @AfterEach
    void tearDown() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
