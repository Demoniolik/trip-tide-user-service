package com.travel.trip.tide.user.controller;

import com.travel.trip.tide.user.model.UserResponseModel;
import com.travel.trip.tide.user.model.UserUpdateRequestModel;
import com.travel.trip.tide.user.model.UserUpdateResponseModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationRequestModel;
import com.travel.trip.tide.user.model.registration.UserRegistrationResponseModel;
import com.travel.trip.tide.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private static final String UPDATE_USER_HINT
            = "update";
    private static final String GET_USER_BY_EMAIL_HINT
            = "getUserByEmail";

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
        return ResponseEntity.ok(applyHateous(userService.getUserById(id)));
    }

    @GetMapping("/search/email")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponseModel> getUserByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(applyHateous(userService.getUserByEmail(email)));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserRegistrationResponseModel> register(
            @Valid @RequestBody UserRegistrationRequestModel userRegistrationModel) {
        return new ResponseEntity<>(
                applyHateous(userService.register(userRegistrationModel)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserUpdateResponseModel> update(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequestModel userUpdateModel) {
        return ResponseEntity.ok(
                applyHateous(id, userService.updateUser(id, userUpdateModel))
        );
    }

    private UserRegistrationResponseModel applyHateous(
            UserRegistrationResponseModel userModel) {
        var slefLink = linkTo(UserController.class)
                .slash(userModel.getId())
                .withSelfRel();
        var updateUserDetails = linkTo(methodOn(UserController.class)
                .update(userModel.getId(), new UserUpdateRequestModel())).withRel(UPDATE_USER_HINT);
        var getUserByEmail = linkTo((methodOn(UserController.class)
                .getUserByEmail(userModel.getEmail()))).withRel(GET_USER_BY_EMAIL_HINT);
        userModel.add(slefLink, updateUserDetails, getUserByEmail);

        return userModel;
    }

    private UserResponseModel applyHateous(
            UserResponseModel userModel) {
        var slefLink = linkTo(UserController.class)
                .slash(userModel.getId())
                .withSelfRel();
        var updateUserDetails = linkTo(methodOn(UserController.class)
                .update(userModel.getId(), new UserUpdateRequestModel()))
                .withRel(UPDATE_USER_HINT);
        var getUserByEmail = linkTo((methodOn(UserController.class)
                .getUserByEmail(userModel.getEmail())))
                .withRel(GET_USER_BY_EMAIL_HINT);
        userModel.add(slefLink, updateUserDetails, getUserByEmail);

        return userModel;
    }

    private UserUpdateResponseModel applyHateous(
            String id,
            UserUpdateResponseModel userModel) {
        var slefLink = linkTo(UserController.class)
                .slash(id)
                .withSelfRel();
        var updateUserDetails = linkTo(methodOn(UserController.class)
                .update(id, new UserUpdateRequestModel()))
                .withRel(UPDATE_USER_HINT);
        var getUserByEmail = linkTo((methodOn(UserController.class)
                .getUserByEmail(userModel.getEmail())))
                .withRel(GET_USER_BY_EMAIL_HINT);
        userModel.add(slefLink, updateUserDetails, getUserByEmail);

        return userModel;
    }

}