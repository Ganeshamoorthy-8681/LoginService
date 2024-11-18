package com.LoginService.login.service;

import com.LoginService.login.entity.User;
import com.LoginService.login.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService  implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = usersRepo.findByEmail(email);
        if(user != null){
            return new com.LoginService.login.entity.UserDetails(user);
        }
        else {
            throw new UsernameNotFoundException("User not found!!");
        }
    }
}
