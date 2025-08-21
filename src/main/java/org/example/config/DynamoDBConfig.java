// src/main/java/org/example/config/DynamoDBConfig.java
package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DynamoDBConfig {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBConfig.class);

    @Value("${aws.secretsManagerSecretId}")
    private String secretId;

    @Bean
    public DynamoDbClient dynamoDbClient() throws Exception {
        Region secretsManagerRegion = Region.of("ap-south-1");
        SecretsManagerClient secretsClient = SecretsManagerClient.builder()
                .region(secretsManagerRegion)
                .build();

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();

        GetSecretValueResponse response;
        try {
            response = secretsClient.getSecretValue(request);
        } catch (Exception e) {
            logger.error("Error fetching secret from Secrets Manager", e);
            throw e;
        }

        String secretJson = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> secrets;
        try {
            secrets = mapper.readValue(secretJson, Map.class);
        } catch (Exception e) {
            throw e;
        }

        return DynamoDbClient.builder()
                .region(Region.of(secrets.get("region")))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        secrets.get("accessKeyId"),
                                        secrets.get("secretAccessKey")
                                )
                        )
                )
                .build();
    }
}