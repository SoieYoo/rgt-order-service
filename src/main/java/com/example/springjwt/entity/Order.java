package com.example.springjwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@SuperBuilder
public class Order extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long tableId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }

    public static Order createOrder(User user, Long tableId, List<OrderItem> orderItems){
        Order order = new Order();
        order.setUser(user);
        order.setTableId(tableId);
        order.setItems(orderItems);
        order.setStatus(Order.Status.CONFIRMED);

        // 각 OrderItem 에 대해 Order 설정
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        return order;
    }
}