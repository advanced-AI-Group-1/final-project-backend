package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.entity.EmailVerificationTokenEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.EmailVerificationTokenRepository;
import com.example.finalproject.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @GetMapping("/verify")
    public void verifyEmail(@RequestParam("token") String token, HttpServletResponse response)
        throws IOException {
        System.out.println("Received token: [" + token + "]");
        EmailVerificationTokenEntity tokenEntity = tokenRepository.findByToken(token);

        if (tokenEntity == null) {
            response.sendRedirect("http://localhost:3000/auth/verify-fail");
            return;
        }
        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            response.sendRedirect("http://localhost:3000/auth/verify-fail");
            return;
        }

        UserEntity user = tokenEntity.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        // tokenRepository.delete(tokenEntity);

//        response.sendRedirect("http://localhost:3000/auth/verified-success");
        response.sendRedirect("http://localhost:3000/auth/verified-success?token=" + token);
    }
}
