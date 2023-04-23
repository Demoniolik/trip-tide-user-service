package com.travel.trip.tide.user.controller;

import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationResponseModel;
import com.travel.trip.tide.user.model.UserResponseModel;
import com.travel.trip.tide.user.model.UserUpdateRequestModel;
import com.travel.trip.tide.user.model.UserUpdateResponseModel;
import com.travel.trip.tide.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseModel>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseModel> getUserById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseModel> getUserByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserRegistrationResponseModel> register(
            @Valid @RequestBody UserRegistrationRequestModel userRegistrationModel) {
        return new ResponseEntity<>(userService.register(userRegistrationModel),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserUpdateResponseModel> update(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequestModel userUpdateModel) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateModel));
    }

}

