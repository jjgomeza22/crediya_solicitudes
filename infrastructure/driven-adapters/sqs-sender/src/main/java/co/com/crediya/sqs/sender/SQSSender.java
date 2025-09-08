package co.com.crediya.sqs.sender;

import co.com.crediya.log.Log;
import co.com.crediya.log.Status;
import co.com.crediya.model.updateapplication.gateways.SQSSenderGateway;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import co.com.crediya.utils.constants.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SQSSenderGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    @Override
    public Mono<String> sendEmailQueue(String message) {
        var method = Method.SEND_EMAIL_QUEUE;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return Mono.fromCallable(() -> buildRequest(message, properties.queueEmailUrl()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)))
                .map(SendMessageResponse::messageId);
    }

    @Override
    public Mono<String> sendDebtCapacityQueue(String message) {
        var method = Method.SEND_DEBT_CAPACITY_QUEUE;
        Log.logInfo(method, this.getClass().getCanonicalName(), Status.EXECUTED.name());
        return Mono.fromCallable(() -> buildRequest(message, properties.queueDebtCapacityUrl()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(usr -> Log.logInfo(method, this.getClass().getCanonicalName(), Status.FINALIZED.name()))
                .doOnError(err -> Log.logError(method, this.getClass().getCanonicalName(), Status.ERROR.name(), new Exception(err)))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}
