package com.jack.userservice.service.impl;

import com.jack.userservice.dto.UsersDTO;
import com.jack.userservice.service.UsersRedisService;
import com.jack.userservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    private final UsersRedisService usersRedisService;

    @Autowired
    public WalletServiceImpl(UsersRedisService usersRedisService) {
        this.usersRedisService = usersRedisService;
    }

    @Override
    public void updateBalanceStatus(Long userId, String status) {
        UsersDTO user = usersRedisService.getUserFromRedis(userId);
        if (user != null) {
            user.setBalanceStatus(status);
            usersRedisService.saveUserToRedis(user);
        }
    }
}
