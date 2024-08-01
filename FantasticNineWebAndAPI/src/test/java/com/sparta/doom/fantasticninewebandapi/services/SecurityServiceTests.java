package com.sparta.doom.fantasticninewebandapi.services;

import com.sparta.doom.fantasticninewebandapi.models.UserDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.Mockito.when;

public class SecurityServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidEmailLoadByUsernameDoesntThrowException() {
        String email = "jUser@email.com";
        UserDoc userDoc = new UserDoc(email, "John User", "password", new HashSet<>()); // Assume this has the necessary setup
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userDoc));

        Assertions.assertDoesNotThrow(() -> securityService.loadUserByUsername(email));
    }

    @Test
    public void givenInvalidEmailLoadByUsernameThrowsException() {
        String email = "invalidUser@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrowsExactly(UsernameNotFoundException.class,
                () -> securityService.loadUserByUsername(email));
    }
}