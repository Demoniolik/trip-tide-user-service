package com.travel.trip.tide.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestModel {

    @Pattern(regexp = "\\p{L}{3,25}",
            message = "The field should contain only letters and be at least 3 characters long")
    private String firstName;
    @Pattern(regexp = "\\p{L}{3,25}",
            message = "The field should contain only letters and be at least 3 characters long")
    private String lastName;
    @Email
    private String email;
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$",
            message = "The phone number should start from the + and contain the country code")
    private String phoneNumber;

}
