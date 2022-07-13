package com.palette.auth.domain.token;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Getter
@Entity
public class RefreshToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String tokenValue;

    @Column(nullable = false)
    private Date expiryDate;

    public RefreshToken(String email, String tokenValue, Date expiryDate) {
        this.email = email;
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
    }
}