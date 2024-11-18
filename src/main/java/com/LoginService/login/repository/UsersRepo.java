package com.LoginService.login.repository;

import com.LoginService.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UsersRepo extends JpaRepository<User, Integer> {

   User findByRefreshToken(String refreshToken);

   User findByEmail(String email);
}
