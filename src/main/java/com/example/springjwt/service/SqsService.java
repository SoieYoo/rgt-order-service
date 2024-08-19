package com.example.springjwt.service;

import com.example.springjwt.dto.OrderDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
@Slf4j
@Service
@RequiredArgsConstructor
public class SqsService {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    public void sendMessage(OrderDTO orderDTO) {
        try {
            String messageBody = objectMapper.writeValueAsString(orderDTO);
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(sendMessageRequest);
            log.info("Order sent to SQS: {}", orderDTO);
        } catch (JsonProcessingException e) {
            log.error("Error serializing order DTO", e);
            throw new RuntimeException("Could not send order to SQS");
        }
    }
}
