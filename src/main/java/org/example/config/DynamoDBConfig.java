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

@Configuration
public class DynamoDBConfig {

    @Value("${aws.secretsManagerSecretId}")
    private String secretId;

    @Bean
    public DynamoDbClient dynamoDbClient() throws Exception {
        SecretsManagerClient secretsClient = SecretsManagerClient.create();
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();
        GetSecretValueResponse response = secretsClient.getSecretValue(request);
        String secretJson = response.secretString();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> secrets = mapper.readValue(secretJson, Map.class);

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