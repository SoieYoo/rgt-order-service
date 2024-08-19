package com.example.springjwt.service;

import com.example.springjwt.entity.Cart;
import com.example.springjwt.entity.CartItem;
import com.example.springjwt.entity.OrderItem;
import com.example.springjwt.entity.User;
import com.example.springjwt.exception.ApiException;
import com.example.springjwt.repository.CartItemRepository;
import com.example.springjwt.repository.CartRepository;
import com.example.springjwt.type.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;

    public List<OrderItem> getItemsFromCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_NOT_FOUND));
        return cart.getItems().stream().map(this::convertToOrderItem).collect(Collectors.toList());
    }

    private OrderItem convertToOrderItem(CartItem cartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItemId(cartItem.getMenuId());
        orderItem.setQuantity(cartItem.getQuantity());
        return orderItem;
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.CART_NOT_FOUND));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
