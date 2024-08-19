package com.example.springjwt.dto;

import com.example.springjwt.entity.Order;
import com.example.springjwt.entity.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long tableId;
    private String username;
    private Order.Status status;
    private List<OrderItemDTO> items;

    @Getter
    @Setter
    @Builder
    public static class OrderItemDTO {
        private Long menuItemId;
        private int quantity;
    }

    // Order 엔터티를 Order DTO로 변환
    public static OrderDTO fromEntity(Order order) {
        return OrderDTO.builder()
                .orderId(order.getId())
                .tableId(order.getTableId())
                .username(order.getUser().getUsername())
                .status(order.getStatus())
                .items(order.getItems().stream()
                        .map(OrderDTO::toOrderItemDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private static OrderItemDTO toOrderItemDto(OrderItem item) {
        return OrderItemDTO.builder()
                .menuItemId(item.getMenuItemId())
                .quantity(item.getQuantity())
                .build();
    }

}
