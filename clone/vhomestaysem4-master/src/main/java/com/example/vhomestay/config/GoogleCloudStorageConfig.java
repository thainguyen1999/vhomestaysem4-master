package com.example.vhomestay.config;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {
//    private final Resource credentialsLocation = new ClassPathResource("flawless-agency-develop.json");
    private final Resource credentialsLocation = new ClassPathResource("hmong-pavi-village-1c41ed58d3a7.json");
    @Bean
    public Storage storage() throws IOException {
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(credentialsLocation.getInputStream()))
                .build();
        return storageOptions.getService();
    }
}