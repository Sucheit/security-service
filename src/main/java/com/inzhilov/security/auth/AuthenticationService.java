package com.inzhilov.security.auth;

import com.inzhilov.security.config.JwtService;
import com.inzhilov.security.user.Role;
import com.inzhilov.security.user.User;
import com.inzhilov.security.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    JwtService jwtService;

    AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String jtw = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jtw)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        User user = userRepository.findUserByEmail(authenticationRequest.getEmail())
                .orElseThrow();
        String jtw = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jtw)
                .build();
    }
}
