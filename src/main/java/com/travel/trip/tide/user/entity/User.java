package com.travel.trip.tide.user.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@Document("users")
public class User {

    @Id
    @GeneratedValue
    private String id;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String phoneNumber;

    @CreatedDate
    private LocalDate createdAt;
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

}
