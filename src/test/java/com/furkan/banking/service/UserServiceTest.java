package com.furkan.banking.service;

import com.furkan.banking.exception.CustomException;
import com.furkan.banking.model.User;
import com.furkan.banking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    public void testCheckByUsername_ShouldReturnTrue_WhenUsernameExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        boolean result = userService.checkByUsername(user.getUsername());

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
    }

    @Test
    public void testCheckByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        boolean result = userService.checkByUsername(user.getUsername());

        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
    }

    @Test
    public void testCheckByEmail_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        boolean result = userService.checkByEmail(user.getEmail());

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    public void testCheckByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        boolean result = userService.checkByEmail(user.getEmail());

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    public void testCheckByEmailAndIdNot_ShouldReturnTrue_WhenEmailExistsForAnotherUser() {
        UUID anotherUserId = UUID.randomUUID();
        when(userRepository.existsByEmailAndIdNot(user.getEmail(), anotherUserId)).thenReturn(true);

        boolean result = userService.checkByEmailAndIdNot(user.getEmail(), anotherUserId);

        assertTrue(result);
        verify(userRepository, times(1)).existsByEmailAndIdNot(user.getEmail(), anotherUserId);
    }

    @Test
    public void testCheckByEmailAndIdNot_ShouldReturnFalse_WhenEmailDoesNotExistForAnotherUser() {
        UUID anotherUserId = UUID.randomUUID();
        when(userRepository.existsByEmailAndIdNot(user.getEmail(), anotherUserId)).thenReturn(false);

        boolean result = userService.checkByEmailAndIdNot(user.getEmail(), anotherUserId);

        assertFalse(result);
        verify(userRepository, times(1)).existsByEmailAndIdNot(user.getEmail(), anotherUserId);
    }

    @Test
    public void testSaveUser_ShouldSaveUserSuccessfully() {
        assertDoesNotThrow(() -> userService.saveUser(user));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSaveUser_ShouldThrowCustomException_WhenSaveFails() {
        doThrow(new DataIntegrityViolationException("Duplicate entry")).when(userRepository).save(user);

        CustomException exception = assertThrows(CustomException.class, () -> userService.saveUser(user));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        assertEquals("An unexpected error occurred while saving user.", exception.getMessage());
        verify(userRepository, times(1)).save(user);
    }
}
