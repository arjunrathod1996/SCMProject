package com.SCM.cartControllerTest;


import com.SCM.cartItem.CartController;
import com.SCM.cartItem.CartItemRepository;
import com.SCM.cartItem.CartService;
import com.SCM.controllers.Merchant;
import com.SCM.entities.User;
import com.SCM.photo.PhotoMerchantRepository;
import com.SCM.repository.MerchantRepository;
import com.SCM.repository.UserRepository;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private PhotoMerchantRepository photoMerchantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    public void testAddToCart_Success() {
        // Arrange
        String shortLink = "merchant123";
        String staffLink = "staff123";
        Long photoMerchantId = 1L;
        int quantity = 2;

        User mockUser = new User();
        mockUser.setId(100L);
        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(200L);

        when(userRepository.findByStaffLink(staffLink)).thenReturn(mockUser);
        when(merchantRepository.findByShortLink(shortLink)).thenReturn(mockMerchant);
        doNothing().when(cartService).addToCart(mockUser.getId(), photoMerchantId, quantity);

        // Act
        ResponseEntity<String> response = cartController.addToCart(shortLink, staffLink, photoMerchantId, quantity);

        // Assert
        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody().equals("Item added to cart");
    }

    @Test
    public void testAddToCart_UserNotFound() {
        // Arrange
        when(userRepository.findByStaffLink("staffLink")).thenReturn(null);

        // Act
        ResponseEntity<String> response = cartController.addToCart("shortLink", "staffLink", 1L, 1);

        // Assert
        assert response.getStatusCodeValue() == 404;
        assert response.getBody().equals("User not found");
    }

    @Test
    public void testAddToCart_MerchantNotFound() {
        User mockUser = new User();
        mockUser.setId(1L);

        when(userRepository.findByStaffLink("staffLink")).thenReturn(mockUser);
        when(merchantRepository.findByShortLink("shortLink")).thenReturn(null);

        ResponseEntity<String> response = cartController.addToCart("shortLink", "staffLink", 1L, 1);

        assert response.getStatusCodeValue() == 404;
        assert response.getBody().equals("Merchant not found");
    }

    @Test
    public void testAddToCart_ExceptionThrown() {
        User mockUser = new User();
        mockUser.setId(1L);
        Merchant mockMerchant = new Merchant();
        mockMerchant.setId(1L);

        when(userRepository.findByStaffLink("staffLink")).thenReturn(mockUser);
        when(merchantRepository.findByShortLink("shortLink")).thenReturn(mockMerchant);
        doThrow(new RuntimeException("DB Error")).when(cartService).addToCart(any(), any(), anyInt());

        ResponseEntity<String> response = cartController.addToCart("shortLink", "staffLink", 1L, 1);

        assert response.getStatusCode().is5xxServerError();
        assert response.getBody().contains("Error occurred while adding item to cart");
    }
}

