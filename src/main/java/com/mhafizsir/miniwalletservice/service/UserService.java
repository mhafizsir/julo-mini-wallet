package com.mhafizsir.miniwalletservice.service;

import com.mhafizsir.miniwalletservice.entity.User;
import com.mhafizsir.miniwalletservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isCustomerExists(String customerXid) {

        return userRepository.existsUserByCustomerXid(customerXid);
    }

    public User create(String customerXid) {

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .customerXid(customerXid)
                .token(UUID.randomUUID().toString().replace("-", ""))
                .balance(BigDecimal.ZERO)
                .createdAt(now)
                .enabled(false)
                .tokenExpiredAt(now.plusDays(7))
                .build();
        return userRepository.save(user);
    }

    public User update(User user) {

        return userRepository.save(user);
    }

    public Optional<User> getByUserId(UUID userId) {

        return userRepository.findById(userId);
    }
}
