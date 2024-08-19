package com.example.springjwt.service;
import com.example.springjwt.dto.OrderDTO;
import com.example.springjwt.entity.Order;
import com.example.springjwt.entity.OrderItem;
import com.example.springjwt.entity.User;
import com.example.springjwt.exception.ApiException;
import com.example.springjwt.repository.OrderRepository;
import com.example.springjwt.repository.UserRepository;
import com.example.springjwt.type.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("유효한 요청으로 주문 생성")
    void placeOrder_ShouldCreateOrder_WhenValidRequest() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        List<OrderItem> orderItems = Collections.singletonList(new OrderItem());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cartService.getItemsFromCart(anyLong())).thenReturn(orderItems);
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // When
        Order result = orderService.placeOrder("testuser", 1L);

        // Then
        assertNotNull(result);
        verify(cartService, times(1)).clearCart(user.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("사용자를 찾을 수 없는 경우 예외 발생")
    void placeOrder_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                orderService.placeOrder("unknownuser", 1L)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("장바구니가 비어 있는 경우 예외 발생")
    void placeOrder_ShouldThrowException_WhenCartIsEmpty() {
        // Given
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cartService.getItemsFromCart(anyLong())).thenReturn(Collections.emptyList());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                orderService.placeOrder("testuser", 1L)
        );

        assertEquals(ErrorCode.EMPTY_CART, exception.getErrorCode());
    }

    @Test
    @DisplayName("캐시에 주문이 있는 경우 OrderDTO 반환")
    void getOrderById_ShouldReturnOrderDTO_WhenOrderExistsInCache() {
        // Given
        OrderDTO cachedOrder = new OrderDTO();
        when(valueOperations.get(anyString())).thenReturn(cachedOrder);

        // When
        OrderDTO result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(cachedOrder, result);
        verify(orderRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("주문을 찾을 수 없는 경우 예외 발생")
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = assertThrows(ApiException.class, () ->
                orderService.getOrderById(1L)
        );

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }
}