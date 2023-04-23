package com.travel.trip.tide.user.model.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponseModel {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
