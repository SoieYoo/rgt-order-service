package com.example.springjwt.service;

import com.example.springjwt.dto.OrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqsOrderProcessor {

    private final SqsClient sqsClient;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;



    @Scheduled(fixedRate = 5000)
    public void processOrders() {
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5) // Batch size
                .build();

        List<Message> messages = sqsClient.receiveMessage(receiveMessageRequest).messages();

        for (Message message : messages) {
            processOrderMessage(message);
            sqsClient.deleteMessage(b -> b.queueUrl(queueUrl).receiptHandle(message.receiptHandle()));
        }
    }

    private void processOrderMessage(Message message) {
        try {
            OrderDTO orderDTO = objectMapper.readValue(message.body(), OrderDTO.class);
            orderService.placeOrder(orderDTO.getUsername(), orderDTO.getTableId());
            log.info("Processed order: {}", orderDTO);
        } catch (JsonProcessingException e) {
            log.error("Failed to process order message", e);
        }
    }
}
