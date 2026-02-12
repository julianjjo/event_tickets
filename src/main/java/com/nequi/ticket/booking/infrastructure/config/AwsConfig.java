package com.nequi.ticket.booking.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class AwsConfig {

        private static final Logger log = LoggerFactory.getLogger(AwsConfig.class);
        private static final ObjectMapper objectMapper = new ObjectMapper();

        @Value("${aws.endpoint_url}")
        private String endpoint;

        @Value("${aws.region}")
        private String region;

        private static final String SECRET_NAME = "aws/credentials";

        @Bean
        public SecretsManagerAsyncClient secretsManagerAsyncClient() {
                log.info("Configuring SecretsManager with endpoint: {} and region: {}", endpoint, region);
                return SecretsManagerAsyncClient.builder()
                                .endpointOverride(URI.create(endpoint))
                                .region(Region.of(region))
                                .credentialsProvider(StaticCredentialsProvider.create(
                                                AwsBasicCredentials.create("test", "test")))
                                .build();
        }

        @Bean
        public AwsCredentialsProvider awsCredentialsProvider(SecretsManagerAsyncClient secretsClient) {
                return () -> {
                        try {
                                String secret = secretsClient
                                                .getSecretValue(GetSecretValueRequest.builder().secretId(SECRET_NAME)
                                                                .build())
                                                .thenApply(response -> response.secretString())
                                                .join();

                                JsonNode node = objectMapper.readTree(secret);
                                String accessKey = node.get("accessKey").asText();
                                String secretKey = node.get("secretKey").asText();

                                return AwsBasicCredentials.create(accessKey, secretKey);
                        } catch (Exception e) {
                                log.error("Failed to retrieve credentials from SecretsManager");
                                throw new RuntimeException(e);
                        }
                };
        }

        @Bean
        public DynamoDbAsyncClient dynamoDbAsyncClient(AwsCredentialsProvider credentialsProvider) {
                return DynamoDbAsyncClient.builder()
                                .endpointOverride(URI.create(endpoint))
                                .region(Region.of(region))
                                .credentialsProvider(credentialsProvider)
                                .build();
        }

        @Bean
        public SqsAsyncClient sqsAsyncClient(AwsCredentialsProvider credentialsProvider) {
                return SqsAsyncClient.builder()
                                .endpointOverride(URI.create(endpoint))
                                .region(Region.of(region))
                                .credentialsProvider(credentialsProvider)
                                .build();
        }
}
