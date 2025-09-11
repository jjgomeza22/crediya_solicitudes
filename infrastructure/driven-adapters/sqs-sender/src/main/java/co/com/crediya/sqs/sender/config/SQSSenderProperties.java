package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapters.sqs")
public record SQSSenderProperties(
     String region,
     String queueEmailUrl,
     String queueDebtCapacityUrl,
     String queueApprovedReportUrl,
     String endpoint
){
}
