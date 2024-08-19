package com.example.springjwt.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
public class SqsConfig {
    public SqsClient createSqsClient() {
        return SqsClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }
}
