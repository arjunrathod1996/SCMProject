package com.SCM.enumHelper;

import com.SCM.cartItem.CartItem.CartItemStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class EnumHelper {

    public List<CartItemStatus> getCartItemStatuses() {
        return Arrays.asList(CartItemStatus.values());
    }
}