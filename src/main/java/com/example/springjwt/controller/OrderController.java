package com.example.springjwt.controller;

import static com.example.springjwt.type.ResponseMessage.ORDER_PLACED_SUCCESS;

import com.example.springjwt.dto.ApiResponse;
import com.example.springjwt.dto.OrderDTO;
import com.example.springjwt.dto.PlaceOrderRequest;
import com.example.springjwt.entity.Order;
import com.example.springjwt.service.OrderService;
import com.example.springjwt.service.SqsService;
import com.example.springjwt.type.ResponseMessage;
import com.example.springjwt.type.ResponseStatus;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SqsService sqsService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(@RequestBody PlaceOrderRequest request,
                                                            Principal principal) {

        String username = principal.getName();

        Order order = orderService.placeOrder(username, request.getTableId());

        sqsService.sendMessage(OrderDTO.fromEntity(order));

        return ResponseEntity.ok(
                new ApiResponse<>(
                        ResponseStatus.SUCCESS.getStatus()
                        , ORDER_PLACED_SUCCESS.getMessage()
                        , OrderDTO.fromEntity(order)
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long orderId) {
            OrderDTO orderDTO = orderService.getOrderById(orderId);

            return ResponseEntity.ok(
                    new ApiResponse<>(ResponseStatus.SUCCESS.getStatus()
                            , ResponseMessage.ORDER_RETRIEVED_SUCCESS.getMessage()
                            , orderDTO)
            );

    }
}
