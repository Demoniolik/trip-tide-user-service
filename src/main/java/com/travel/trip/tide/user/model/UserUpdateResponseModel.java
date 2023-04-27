package com.travel.trip.tide.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserUpdateResponseModel extends RepresentationModel<UserUpdateResponseModel> {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
