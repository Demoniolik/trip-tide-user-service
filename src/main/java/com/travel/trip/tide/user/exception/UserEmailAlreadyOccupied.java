package com.travel.trip.tide.user.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailAlreadyOccupied extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;

}
