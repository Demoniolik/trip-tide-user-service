package com.travel.trip.tide.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseModel {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;

}
