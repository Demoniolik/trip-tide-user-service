package com.travel.trip.tide.user.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationErrorResponseModel {

    private String errorCode;
    private List<String> errors;

}
