package com.example.uploadingfiles.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@EnableConfigurationProperties(value = StorageProperties.class)
@TestPropertySource("classpath:application.yml")
@SpringBootTest
class StoragePropertiesTest {

    @Autowired
    private StorageProperties storageProperties;

    @Test
    public void testStorageProperties() throws Exception {
        System.out.println("storageProperties.getLocation() = " + storageProperties.getLocation());
        assertEquals("src/main/resources/static/files", storageProperties.getLocation());
    }

}