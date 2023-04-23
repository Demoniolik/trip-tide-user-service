package com.travel.trip.tide.user.model.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseModel {

    private String errorCode;
    private String errorDescription;

}
