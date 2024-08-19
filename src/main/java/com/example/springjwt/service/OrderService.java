package com.example.springjwt.service;

import com.example.springjwt.dto.OrderDTO;
import com.example.springjwt.entity.Order;
import com.example.springjwt.entity.OrderItem;
import com.example.springjwt.entity.User;
import com.example.springjwt.exception.ApiException;
import com.example.springjwt.repository.OrderRepository;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.type.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final RedisTemplate<String, Object> redisTemplate;

    public Order placeOrder(String username, Long tableId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<OrderItem> orderItems = cartService.getItemsFromCart(user.getId());

        if (orderItems.isEmpty()) {
            throw new ApiException(ErrorCode.EMPTY_CART);
        }

        Order order = Order.createOrder(user, tableId, orderItems);

        orderRepository.save(order);

        // 장바구니 비우기
        cartService.clearCart(user.getId());

        return order;
    }

    public OrderDTO getOrderById(Long orderId) {
        String cacheKey = "order:" + orderId;
        OrderDTO cachedOrder = (OrderDTO) redisTemplate.opsForValue().get(cacheKey);

        log.info("order ID {} from Redis cache key {}", orderId, cacheKey);

        if (cachedOrder != null) {
            return cachedOrder;
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException(ErrorCode.ORDER_NOT_FOUND));

        OrderDTO orderDTO = OrderDTO.fromEntity(order);

        redisTemplate.opsForValue().set(cacheKey, orderDTO);

        return orderDTO;
    }
}
