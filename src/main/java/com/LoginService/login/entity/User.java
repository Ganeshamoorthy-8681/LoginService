package com.LoginService.login.entity;

import com.LoginService.login.enums.UserProviderEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// For JPQL Query
@Entity(name="users")

//For table name
@Table(name = "users")

@Getter
@Setter
@NoArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column()
    private String username;

    @Column()
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name="provider")
    @Enumerated(EnumType.STRING)
    private UserProviderEnum provider;

    @Column(name = "refresh_token")
    private String refreshToken;


    @Column(name = "created",updatable = false,nullable = false)
    private long createdOn;

    @Column(name = "updated")
    private long updatedOn;


    @PrePersist
    protected void updateCreatedOn() {
        createdOn = Instant.now().toEpochMilli();
    }

    @PreUpdate
    private void  updateUpdatedOn(){
        updatedOn = Instant.now().toEpochMilli();
    }
}
