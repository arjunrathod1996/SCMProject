package com.SCM.ratingAndReview;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SCM.cartItem.CartItem;
import com.SCM.cartItem.CartItemRepository;

@Service
public class RatingService {

    @Autowired
    private CartItemRepository cartItemRepository;

    public Double calculateAverageRating(Long merchantId, Long userId, Long photoId) {
        List<CartItem> cartItems = cartItemRepository.findAllByMerchantIdAndUserId(merchantId, userId);
        
        return cartItems.stream()
            .filter(cartItem -> cartItem.getPhotoMerchant().getId().equals(photoId))
            .filter(cartItem -> cartItem.getRating() != null)
            .mapToInt(CartItem::getRating)
            .average()
            .orElse(0.0); // Return 0.0 if no ratings are found
    }
}

